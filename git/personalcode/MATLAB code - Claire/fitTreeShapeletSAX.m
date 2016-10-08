function rootNode = fitTreeShapeletSAX(classNames, classVector, featureDataMatrixSAX, ...
    featureMatrixFeatures, featureNames, numCandidatesPerClass, minLength, maxLength, lengthDiff, numChannels, ...
    maxDepth, classPercentageThreshold, distMat)
    % classNames - cell array representing the names of the classes
    % featureNames - cell array representing the names of the featuress
    % classVector - 1 x numSamples cell array that represents the actual
    %               class for each of the samples
    % featureMatrix - numSamples x numFeatures matrix that represents the
    %                 feature array for each sample
    % maxDepth - the maximum depth of the tree (stopping condition)
    % classPercentageThreshold - the threshold value of how large a
    %                            percentage one class can be in a node
    %                            (stopping condition)
    
    t1 = tic;
    numClasses = length(classNames);
    numSamples = size(featureDataMatrixSAX, 1);
    numFeatures = size(featureMatrixFeatures, 2);
    
    classNameDict = struct;
    
    % Create a map from class names to class indices
    for class = 1:numClasses
        classNameDict.(char(classNames{class})) = class;
    end
    
    % Before continuing, we want to convert classVector to use
    % class indices since string manipulation is expensive
    numericClassVector = zeros(numSamples, 1);
    classCounts = zeros(numClasses, 1);
    for sample = 1:numSamples
        classId = classNameDict.(char(classVector{sample}));
        numericClassVector(sample) = classId;
        classCounts(classId) = classCounts(classId) + 1;
    end
    
    % Append the class vector to the end of the feature matrix for
    % convenience in splitting data
    featureDataMatrixSAX = [featureDataMatrixSAX, numericClassVector];
    featureMatrixFeatures = [featureMatrixFeatures, numericClassVector];
    
    % Recursively build the tree
    rootNode = CalcNode(featureDataMatrixSAX, featureMatrixFeatures, 0, maxDepth, ...
        classCounts, classPercentageThreshold, distMat, minLength, maxLength, ...
        lengthDiff, numChannels, numCandidatesPerClass, numFeatures, featureNames);
    toc(t1);
end

function node = CalcNode(featureDataMatrixSAX, featureMatrixFeatures, curDepth, maxDepth, ...
    classCounts, classPercentageThreshold, distMat, minLength, maxLength, lengthDiff, ...
    numChannels, numCandidatesPerClass, numFeatures, featureNames)

    % First we need to check our stopping conditions
    classPercentage = classCounts / sum(classCounts);
    if (curDepth == maxDepth || sum(classCounts > numCandidatesPerClass) == 0 || ...
            sum(classPercentage > classPercentageThreshold) > 0)
        % Since we have stopped, simply return a leaf
        [~, classIndex] = max(classCounts);
        node = TreeLeafShapelet(classIndex, classCounts);
    else
        % Sample length is 1 less than the number of columns in the feature
        % matrix since the last column is the class
        sampleLength = size(featureDataMatrixSAX, 2) - 1;
        
        % Only consider the classes where we have enough data
        numClasses = sum(classCounts > numCandidatesPerClass);
        
        dataset = zeros(numCandidatesPerClass * numClasses, sampleLength + 1);
        startIndex = 0;
        dataClass = 1;
        for class = 1:length(classCounts)
            if (classCounts(class) > numCandidatesPerClass)
                % Generate a random vector of indices
                indices = randperm(classCounts(class));
                indices = indices(1:numCandidatesPerClass);

                for i = 1:numCandidatesPerClass
                    candidateId = (dataClass - 1) * numCandidatesPerClass + i;
                    dataset(candidateId,1:sampleLength) = ...
                        featureDataMatrixSAX(startIndex + indices(i),1:sampleLength);
                    dataset(candidateId,sampleLength + 1) = dataClass;
                end

                startIndex = startIndex + classCounts(class);
                dataClass = dataClass + 1;
            end
        end
        
        % Here we want to calculate all of the possible shapelets
        shapelets = cell(1, maxLength - minLength + 1);
        if (lengthDiff > 0)
            lengthDiff = -lengthDiff;
        end
        for candidateLength = maxLength:lengthDiff:minLength
            shapelets{maxLength - round(candidateLength) + 1} = ...
                GenerateCandidates(dataset, numChannels, round(candidateLength));
        end
        
        numSamples = size(featureDataMatrixSAX, 1); 
        
        % Calculate the best split at the current node
        optimalInformationGain = 0.0;
        optimalSeparationGap = 0.0;
        optimalSplitShapelet = [];
        optimalSplitChannel = 0;
        optimalSplitThreshold = 0.0;
        optimalSplitFeature = -1;
        
        % The first thing we need to is calculate the initial entropy
        classInvFreq = sum(classCounts) ./ classCounts;
        classInvFreq(classInvFreq == Inf) = 0;
        initialEntropy = CalcEntropy(classCounts, classInvFreq);
        
        % Now we need to try every shapelet and pick the one that gives the
        % greatest information gain
        
        % Generate random indices for each of the shapelets and initialize
        % the necessary data structures for this analysis
        numShapeletLengths = length(shapelets);
        randomIndices = cell(numShapeletLengths, numChannels);
        infoGainMatrix = cell(numShapeletLengths, numChannels);
        numActiveCombinations = 0;
        
        for i = 1:numShapeletLengths
            curShapelets = shapelets{i};
            curNumChannels = length(curShapelets);
            
            for channel = 1:curNumChannels
                curChannelShapelets = curShapelets{channel};
                randomIndices{i, channel} = randperm(size(curChannelShapelets, 1));
                numActiveCombinations = numActiveCombinations + 1;
                initialInfo = cell(1, 4);
                initialInfo{1} = 0.0; % Information gain
                initialInfo{2} = 0.0; % Separation gap
                initialInfo{3} = [];  % Split shapelet
                initialInfo{4} = 0.0; % Split threshold
                
                infoGainMatrix{i, channel} = initialInfo;
            end
        end
        
        % Each iteration we will reduce the search space in half so keep
        % going while there are still active combinations
        iteration = 0;
        while (numActiveCombinations > 0)
            disp(['Iteration ', num2str(iteration), ' with ', num2str(numActiveCombinations), ' active combinations']);
            
            for i = 1:numShapeletLengths
                curShapelets = shapelets{i};
                curNumChannels = length(curShapelets);

                for channel = 1:curNumChannels
                    if (~isempty(randomIndices{i, channel}))
                        curChannelShapelets = curShapelets{channel};
                        shapeletLength = size(curChannelShapelets, 2);
                        curRandomIndices = randomIndices{i, channel};
                        curInfoGain = infoGainMatrix{i, channel};
                        numCurRandomIndices = length(curRandomIndices);
                        
                        % This 100 is chosen here so that with 99%
                        % probability we are guaranteed to get a top 5%
                        % shapelet on the 1st iteration, top 2% shapelet on
                        % the 2nd and so on
                        for shapeletIndex = 1:100
                            realShapeletIndex = shapeletIndex + 100 * iteration;
                            if (realShapeletIndex > numCurRandomIndices)
                                break;
                            end
                            shapelet = curChannelShapelets(curRandomIndices(realShapeletIndex), :);

                            [curInformationGain, curSplitThreshold, curSeparationGap] = TryShapelet(...
                                featureDataMatrixSAX, shapelet, initialEntropy, ...
                                sampleLength, channel, numChannels, classCounts, distMat, classInvFreq);
                            
                            if (curInformationGain > curInfoGain{1} || ...
                                    (curInformationGain == curInfoGain{1} && ...
                                    curSeparationGap > curInfoGain{2}))
                                
                                disp(['Information gain: ', num2str(curInformationGain), ...
                                    ' Separation gap: ', num2str(curSeparationGap)]);
                                
                                curInfoGain{1} = curInformationGain;
                                curInfoGain{2} = curSeparationGap;
                                curInfoGain{3} = shapelet;
                                curInfoGain{4} = curSplitThreshold;
                            end
                        end
                        
                        infoGainMatrix{i, channel} = curInfoGain;
                            
                        disp(['Finished shapelet ', num2str(shapeletLength), ' channel ', ...
                            num2str(channel), ' with optimal information gain = ', num2str(curInfoGain{1})]);
                    end
                end   
            end
            
            % Now that we have gone through one iteration we want to prune
            % the lower half of the combinations
            allInfoGains = zeros(1, numActiveCombinations);
            index = 1;
            for i = 1:numShapeletLengths
                for channel = 1:numChannels
                    if (~isempty(randomIndices{i, channel}))
                        curInfoGain = infoGainMatrix{i, channel};
                        allInfoGains(index) = curInfoGain{1};
                        index = index + 1;
                        
                        if (curInfoGain{1} > optimalInformationGain || ...
                            (curInfoGain{1} == optimalInformationGain && ...
                            curInfoGain{2} > optimalSeparationGap))
                        
                            optimalInformationGain = curInfoGain{1};
                            optimalSeparationGap = curInfoGain{2};
                            optimalSplitShapelet = curInfoGain{3};
                            optimalSplitChannel = channel;
                            optimalSplitThreshold = curInfoGain{4};
                        end 
                    end
                end
            end
            
            infoGainCutoff = median(allInfoGains);
            oldNumActiveCombinations = numActiveCombinations;
            
            for i = 1:numShapeletLengths
                for channel = 1:numChannels
                    if (~isempty(randomIndices{i, channel}))
                        curInfoGain = infoGainMatrix{i, channel}{1};
                        
                        if (curInfoGain < infoGainCutoff)
                            randomIndices{i, channel} = [];
                            numActiveCombinations = numActiveCombinations - 1;
                        end
                    end
                end
            end
            
            if (oldNumActiveCombinations == numActiveCombinations)
                break;
            end
            
            iteration = iteration + 1;
        end

        for feature = 1:numFeatures
            % Iterate through each feature and each "sensible" split to find
            % the optimal information gain

            % The feature values need to be sorted to find the right split
            sortedFeatureMatrixFeatures = sortrows(featureMatrixFeatures,feature);
            sortedFeatureValues = sortedFeatureMatrixFeatures(:,feature);
            sortedClasses = sortedFeatureMatrixFeatures(:,numFeatures + 1);

            numClasses = length(classCounts);
            currentClass = sortedClasses(1);
            splitClassCounts = zeros(numClasses, 1);
            for sample = 1:numSamples
                % A "sensible" split will be defined as the average between the
                % feature values where the class is changed
                if (currentClass ~= sortedClasses(sample));
                    % Once a split is found, we need to calculate the
                    % information gain and record the split if it's the best
                    % information gain we have found so far
                    residualClassCounts = classCounts - splitClassCounts;

                    splitEntropy = CalcEntropy(splitClassCounts, classInvFreq);
                    residualEntropy = CalcEntropy(residualClassCounts, classInvFreq);

                    splitFraction = (sample - 1) / numSamples;
                    residualFraction = 1.0 - splitFraction;

                    informationGain = initialEntropy - ...
                        (splitFraction * splitEntropy + residualFraction * residualEntropy);

                    if (informationGain > optimalInformationGain)
                        optimalInformationGain = informationGain;
                        disp(['Information gain: ', num2str(informationGain)]);
                        optimalSplitFeature = feature;
                        optimalSplitThreshold = ...
                            (sortedFeatureValues(sample - 1) + sortedFeatureValues(sample)) / 2;
                    elseif (informationGain == optimalInformationGain)
                        disp('Interesting case - need to investigate more...');
                    end
                end

                currentClass = sortedClasses(sample);
                splitClassCounts(currentClass) = splitClassCounts(currentClass) + 1;
            end
        end
        
        % Another stopping condition occurs here if we have not found a
        % split that results in information gain
        if (optimalInformationGain == 0.0)
            % Since we have stopped, simply return a tree leaf
            [~, classIndex] = max(classCounts);
            node = TreeLeafShapelet(classIndex, classCounts);
        else
            if (optimalSplitFeature > 0)
                % At this point, we know the optimal split feature and threshold. Now
                % we need to put this into a TreeNode object and split the data for
                % further learning
                classMeans = zeros(numClasses, 1);
                classStds = zeros(numClasses, 1);
                for class = 1:numClasses
                    classOptimalFeature = featureMatrixFeatures(featureMatrixFeatures(:,numFeatures + 1) == class, optimalSplitFeature);
                    classMeans(class) = mean(classOptimalFeature);
                    classStds(class) = std(classOptimalFeature);
                end
                node = TreeNode(optimalSplitFeature, char(featureNames{optimalSplitFeature}), ...
                    optimalSplitThreshold, classCounts, classMeans, classStds);

                leftIndices = featureMatrixFeatures(:,optimalSplitFeature) <= optimalSplitThreshold;
                rightIndices = featureMatrixFeatures(:,optimalSplitFeature) > optimalSplitThreshold;
                
                leftFeatureMatrixFeatures = featureMatrixFeatures(leftIndices,:);
                leftFeatureMatrixSAX = featureDataMatrixSAX(leftIndices,:);
                leftClassCounts = zeros(numClasses, 1);
                for class = 1:numClasses
                    leftClassCounts(class) = sum(leftFeatureMatrixFeatures(:,end) == class);
                end
                
                rightFeatureMatrixFeatures = featureMatrixFeatures(rightIndices,:);
                rightFeatureMatrixSAX = featureDataMatrixSAX(rightIndices,:);
                rightClassCounts = zeros(numClasses, 1);
                for class = 1:numClasses
                    rightClassCounts(class) = sum(rightFeatureMatrixFeatures(:,end) == class);
                end
            else
                % At this point, we know the optimal split shapelet and threshold. Now
                % we need to put this into a TreeNode object and split the data for
                % futher learning
                node = TreeNodeShapelet(optimalSplitShapelet, optimalSplitChannel, ...
                    optimalSplitThreshold, classCounts);

                % Split the data by the optimal shapelet
                numClasses = length(classCounts);
                leftFeatureMatrixSAX = zeros(numSamples, sampleLength + 1);
                leftFeatureMatrixFeatures = zeros(numSamples, numFeatures + 1);
                leftClassCounts = zeros(numClasses, 1);
                leftIndex = 0;
                rightFeatureMatrixSAX = zeros(numSamples, sampleLength + 1);
                rightFeatureMatrixFeatures = zeros(numSamples, numFeatures + 1);
                rightClassCounts = zeros(numClasses, 1);
                rightIndex = 0;

                for sample = 1:numSamples
                    dist = CalcMinDistanceSAX(featureDataMatrixSAX(sample, 1:sampleLength), ...
                        optimalSplitShapelet, optimalSplitChannel, numChannels, distMat);
                    class = featureDataMatrixSAX(sample, sampleLength + 1);

                    if (dist < optimalSplitThreshold)
                        % Go left
                        leftIndex = leftIndex + 1;
                        leftFeatureMatrixSAX(leftIndex, :) = featureDataMatrixSAX(sample, :);
                        leftFeatureMatrixFeatures(leftIndex, :) = featureMatrixFeatures(sample, :);
                        leftClassCounts(class) = leftClassCounts(class) + 1;
                    else
                        % Go right
                        rightIndex = rightIndex + 1;
                        rightFeatureMatrixSAX(rightIndex, :) = featureDataMatrixSAX(sample, :);
                        rightFeatureMatrixFeatures(rightIndex, :) = featureMatrixFeatures(sample, :);
                        rightClassCounts(class) = rightClassCounts(class) + 1;
                    end
                end

                leftFeatureMatrixSAX = leftFeatureMatrixSAX(1:leftIndex, :);
                leftFeatureMatrixFeatures = leftFeatureMatrixFeatures(1:leftIndex, :);
                rightFeatureMatrixSAX = rightFeatureMatrixSAX(1:rightIndex, :);
                rightFeatureMatrixFeatures = rightFeatureMatrixFeatures(1:rightIndex, :);
            end
            
            % Recursively build up the tree
            node.LeftChild = ...
                CalcNode(leftFeatureMatrixSAX, leftFeatureMatrixFeatures, curDepth + 1, maxDepth, ...
                leftClassCounts, classPercentageThreshold, ...
                distMat, minLength, maxLength, lengthDiff, numChannels, ...
                numCandidatesPerClass, numFeatures, featureNames);
            node.RightChild = ...
                CalcNode(rightFeatureMatrixSAX, rightFeatureMatrixFeatures, curDepth + 1, maxDepth, ...
                rightClassCounts, classPercentageThreshold, ...
                distMat, minLength, maxLength, lengthDiff, numChannels, ...
                numCandidatesPerClass, numFeatures, featureNames);
            node.LeftChild.Parent = node;
            node.RightChild.Parent = node;
        end
    end
end

function [optimalInformationGain, optimalSplitThreshold, optimalSeparationGap] = ...
    UpdateInformationGain(sampleDistances, classCounts, initialEntropy, classInvFreq)
    % Calculate the optimal information gain and split

    % Sort by the distances
    sampleDistances = sampleDistances(sampleDistances(:,2) ~= 0, :);
    sampleDistances = sortrows(sampleDistances, 1);
    numSamples = size(sampleDistances, 1);
    numClasses = length(classCounts);
    optimalInformationGain = 0.0;
    optimalSeparationGap = 0.0;
    optimalSplitThreshold = 0.0;
    
    % The initial split will contain all of the class counts in the right
    splitClassCounts = zeros(numClasses, 2);
    splitClassCounts(:, 2) = classCounts;
    splitSums = zeros(1, 2);
    splitSums(2) = sum(sampleDistances(:, 1));
    
    for sample = 1:numSamples
        % A "sensible" split will be defined as the average between the
        % feature values where the class is changed
        if (sample > 1 && sampleDistances(sample - 1, 1) ~= sampleDistances(sample, 1))
            threshold = (sampleDistances(sample - 1, 1) + sampleDistances(sample, 1)) / 2;
            [optimalInformationGain, optimalSplitThreshold, optimalSeparationGap]...
                = CalcSampleInformationGain(splitClassCounts, ...
                splitSums, optimalInformationGain, threshold, ...
                initialEntropy, optimalSplitThreshold, optimalSeparationGap, classInvFreq);
        end
        
        % Update the split class counts and sum
        currentDist = sampleDistances(sample, 1);
        currentClass = sampleDistances(sample, 2);
        splitClassCounts(currentClass, 1) = splitClassCounts(currentClass, 1) + 1;
        splitClassCounts(currentClass, 2) = splitClassCounts(currentClass, 2) - 1;
        splitSums(1) = splitSums(1) + currentDist;
        splitSums(2) = splitSums(2) - currentDist;
    end
end

function [optimalInformationGain, optimalSplitThreshold, optimalSeparationGap]...
    = CalcSampleInformationGain(splitClassCounts, splitSums, ...
    optimalInformationGain, threshold, initialEntropy, ...
    optimalSplitThreshold, optimalSeparationGap, classInvFreq)
    % Once a split is found, we need to calculate the
    % information gain and record the split if it's the best
    % information gain we have found so far
    leftSum = sum(splitClassCounts(:,1));
    rightSum = sum(splitClassCounts(:,2));
    leftEntropy = CalcEntropy(splitClassCounts(:,1), classInvFreq);
    rightEntropy = CalcEntropy(splitClassCounts(:,2), classInvFreq);

    leftFraction = leftSum / (leftSum + rightSum);
    rightFraction = 1.0 - leftFraction;

    informationGain = initialEntropy - ...
        (leftFraction * leftEntropy + rightFraction * rightEntropy);

    separationGap = (splitSums(2) / rightSum) - (splitSums(1) / leftSum);

    if (informationGain > optimalInformationGain || ...
        (informationGain == optimalInformationGain && ...
            separationGap > optimalSeparationGap))
        optimalInformationGain = informationGain;
        optimalSeparationGap = separationGap;
        optimalSplitThreshold = threshold;
    end
end

function [foundInformationGain, foundSplitThreshold, foundSeparationGap] = TryShapelet(...
    featureDataMatrix, shapelet, initialEntropy, sampleLength, channel, ...
    numChannels, maxClassCounts, distMat, classInvFreq)

    % At this point we have identified a shapelet to try
    % and now we need to calculate the minimum distance
    % from every sample to this shapelet
    numClasses = length(maxClassCounts);
    numSamples = size(featureDataMatrix, 1);
    sampleDistances = zeros(numSamples, 2);

    % Initialize the class counts
    classCounts = zeros(numClasses, 1);
    
    for sampleIndex = 1:numSamples
        sample = featureDataMatrix(sampleIndex, 1:sampleLength);
        sampleDistance = CalcMinDistanceSAX(sample, shapelet, ...
            channel, numChannels, distMat);
        class = featureDataMatrix(sampleIndex, sampleLength + 1);

        % Assign this distance and the class it came from
        % to our collection
        sampleDistances(sampleIndex, 1) = sampleDistance;
        sampleDistances(sampleIndex, 2) = class;

        classCounts(class) = classCounts(class) + 1;
    end
    
    % Calculate the final information gain
    [foundInformationGain, foundSplitThreshold, foundSeparationGap] = ...
        UpdateInformationGain(sampleDistances, classCounts, initialEntropy, classInvFreq);
end

function candidates = GenerateCandidates(dataset, numChannels, candidateLength)
    % Pick out all of the candidates with the given length out of the
    % dataset
    numSamples = size(dataset, 1);
    sampleLength = size(dataset, 2) - 1;
    channelLength = sampleLength / numChannels;
    candidates = cell(1, numChannels);
    numShapelets = (channelLength - candidateLength + 1) * numSamples;
    
    % Alloc memory
    for channel = 1:numChannels
        candidates{channel} = zeros(numShapelets, candidateLength);
    end
    
    % Slide a length-sized window across each sample keeping in mind
    % channel boundaries
    indices = ones(1, numChannels);
    for sample = 1:numSamples        
        for channel = 1:numChannels
            for start = 1:(channelLength - candidateLength + 1)
                startIndex = start + (channel - 1) * channelLength;
                
                candidates{channel}(indices(channel),1:candidateLength) = ...
                    dataset(sample, startIndex:(startIndex + candidateLength - 1));
                
                indices(channel) = indices(channel) + 1;
            end
        end
    end
end

function entropy = CalcEntropy(classCounts, classInvFreq)
    % Multiply each class by its inverse frequency to keep from
    % undervaluing rare classes
    classCounts = classCounts .* classInvFreq;

    % Remove all of the zero values since they are not needed
    normalizedClassCounts = classCounts(classCounts ~= 0) / sum(classCounts);
    
    entropy = -normalizedClassCounts'*log(normalizedClassCounts);
end