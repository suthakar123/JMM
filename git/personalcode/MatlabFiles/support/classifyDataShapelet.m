function [classificationVector, classificationCertainty, classificationError, ...
    unknownPercent] = classifyDataShapelet(root, featureMatrix, featureMatrixShapelet, ...
    classNames, numChannels, useKatz, unknownThreshold, trueClassVector, distMat)
    display('classifyDataShapelet: Production version 04.30.16');
    % root - a TreeNode object that represents the root of the tree
    % featureMatrix - numSamples x numFeatures matrix that represents the
    %                 feature array for each sample
    % trueClassVector - a cell array of true classification values
    
    numSamples = size(featureMatrixShapelet, 1);
    classificationVector = cell(1, numSamples);
    classificationCertainty = zeros(1, numSamples);
    classIdentities = zeros(1, numSamples);
    correctPredictionCount = 0;
    totalPredictionCount = 0;
    unknownPredictionCount = 0;
    
    % Classify each sample and record the result in classification vector
    for sample = 1:numSamples
        currentSampleShapelet = featureMatrixShapelet(sample,:);
        currentSampleFeatures = featureMatrix(sample,:);
        
        classProbabilities = classifyInstance(root, currentSampleShapelet, ...
            currentSampleFeatures, useKatz, numChannels, distMat);
        
        % Once we have the probabilities, we need to find the maximum to
        % produce a certainty measure
        [classCertainty, classIdentity] = max(classProbabilities);
        
        classificationCertainty(sample) = classCertainty;
        % Check for the unknown classification
        %32-35 45
        if (classCertainty < unknownThreshold || (sample > 1 && (classIdentity ~= classIdentities(sample - 1))))
            classificationVector{sample} = 'Unknown';
            unknownPredictionCount = unknownPredictionCount + 1;
        else
            classificationVector{sample} = classNames{classIdentity};
            totalPredictionCount = totalPredictionCount + 1;
            
            % If we have the true values, 
            if (~isempty(trueClassVector))    
                if (strcmp(trueClassVector{sample}, classificationVector{sample}))
                    correctPredictionCount = correctPredictionCount + 1;
                end
            end
        end
        classIdentities(sample) = classIdentity;
    end
    
    classificationError = 1 - correctPredictionCount / totalPredictionCount;
    unknownPercent = unknownPredictionCount / numSamples;
end

function classProbabilities = classifyInstance(currentNode, sampleShapelet, ...
    sampleFeatures, useKatz, numChannels, distMat)
    % If we are at a leaf, we simply return the 
    if (isa(currentNode, 'TreeLeafShapelet'))
        classProbabilities = currentNode.ClassMembership / sum(currentNode.ClassMembership);
    else
        if (isa(currentNode, 'TreeNodeShapelet'))
            % Figure out which branch of the tree we should take
            nodeShapelet = currentNode.SplitShapelet;
            nodeChannel = currentNode.SplitChannel;
            nodeDistance = CalcMinDistanceSAX(sampleShapelet, nodeShapelet, nodeChannel, numChannels, distMat);
            if (nodeDistance < currentNode.SplitThreshold)
                nextNode = currentNode.LeftChild;
            else
                nextNode = currentNode.RightChild;
            end
            classProbabilities = classifyInstance(nextNode, sampleShapelet, sampleFeatures, useKatz, numChannels, distMat);
        else
            % Figure out which branch of the tree we should take
            nodeFeature = currentNode.SplitFeature;
            if (sampleFeatures(nodeFeature) < currentNode.SplitThreshold)
                nextNode = currentNode.LeftChild;
            else
                nextNode = currentNode.RightChild;
            end
            classProbabilities = classifyInstance(nextNode, sampleShapelet, sampleFeatures, useKatz, numChannels, distMat);
        end
        
        % THIS IS NOT BEING USED RIGHT NOW AND PROBABLY SHOULDN'T BE USED.
        % HOWEVER I AM LEAVING THIS CODE IN CASE WE WANT TO TRY USING KATZ
        % DECISION TREES IN THE FUTURE
%         if (useKatz)
%             Now we need to see if the prediction is within the range of
%             values for that class in this node
%             if (~isWithinRange(currentNode, sample, classIdentity))
%                 The first check we need to is whether the sample is within
%                 the range for any other class
%                 numClasses = length(classProbabilities);
%                 alternativeClassIds = zeros(1, numClasses);
%                 for otherClassId = 1:numClasses
%                     alternativeClassIds(otherClassId) = isWithinRange(currentNode, sample, otherClassId);
%                 end
% 
%                 if (sum(alternativeClassIds) > 0)
%                     In this case, we know that we need to calculate the
%                     prediction for each branch and multiply by weights
%                     if (sample(nodeFeature) < currentNode.SplitThreshold)
%                         Here we know that we already have the left node so
%                         just calculate the right node prediction
%                         leftProb = classProbabilities;
%                         rightProb = classifyInstance(currentNode.RightChild, sample, useKatz);
%                     else
%                         Here we know that we already have the right node so
%                         just calculate the left node prediction
%                         leftProb = classifyInstance(currentNode.LeftChild, sample, useKatz);
%                         rightProb = classProbabilities;
%                     end
% 
%                     Now calculate each of the weights
%                     leftWeight = 0;
%                     rightWeight = 0;
%                     leftClasses = currentNode.LeftChild.ClassMembership;
%                     rightClasses = currentNode.RightChild.ClassMembership;
%                     totalLeft = sum(leftClasses);
%                     totalRight = sum(rightClasses);
%                     for class = 1:numClasses
%                         if (alternativeClassIds(class))
%                             leftWeight = leftWeight + sqrt(leftClasses(class))...
%                                 * leftClasses(class) / totalLeft;
%                             rightWeight = rightWeight + sqrt(rightClasses(class))...
%                                 * rightClasses(class) / totalRight;
%                         end
%                     end
% 
%                     Normalize both weights
%                     weightSum = leftWeight + rightWeight;
%                     leftWeight = leftWeight / weightSum;
%                     rightWeight = rightWeight / weightSum;
% 
%                     Now get the final class probabilities from the weights
%                     and node probabilities
%                     classProbabilities = leftWeight * leftProb + rightWeight * rightProb;
%                 else
%                     In this case, there are no other classes that fit.
%                     Therefore the best we can do is to impose a certainty fine
%                     fine = 0.1 * classCertainty;
%                     delta = fine / (numClasses - 1);
%                     for class = 1:numClasses
%                         if (class == classIdentity)
%                             classProbabilities(class) = classProbabilities(class) - fine;
%                         else
%                             classProbabilities(class) = classProbabilities(class) + delta;
%                         end
%                     end
%                 end
%             end
%         end
    end
end

% THIS IS NOT BEING USED RIGHT NOW AND PROBABLY SHOULDN'T BE USED.
% HOWEVER I AM LEAVING THIS CODE IN CASE WE WANT TO TRY USING KATZ
% DECISION TREES IN THE FUTURE
% function withinRange = isWithinRange(node, sample, classId)
%     % Check if the sample is within the range of the node for the given class
%     featureId = node.SplitFeature;
%     mean = node.ClassMeans(classId);
%     if (isnan(mean))
%         withinRange = false;
%     else
%         std = node.ClassStds(classId);
%         lowerRange = mean - 3 * std;
%         upperRange = mean + 3 * std;
%         withinRange = (sample(featureId) > lowerRange && sample(featureId) < upperRange);
%     end
% end