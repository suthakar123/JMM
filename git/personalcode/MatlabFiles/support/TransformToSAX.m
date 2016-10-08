function [SAXData, betaVec, distMat] = TransformToSAX(data, numChannels, ...
    letterLength, cardinality) 
    disp('TransformToSax: Production version 04.30.16  ');
    N = size(data, 1);
    D = size(data, 2) / numChannels;
    newD = D / letterLength;
    
    % Set up the beta vector
    betaVec = zeros(cardinality - 1, 1);
    %betaVecWaitbar = waitbar(0, 'Setting up beta vector');
    for b = 1:cardinality - 1
        
        betaVec(b) = norminv(b/cardinality, 0, 1);
        %waitbar(b / (cardinality - 1), betaVecWaitbar);
    end
    %delete(betaVecWaitbar);
    
    % Set up the distance matrix
    distMat = zeros(cardinality);
    %distMatWaitbar = waitbar(0, 'Setting up distance matrix');
    for r = 1:cardinality
        for c = 1:cardinality
            if (abs(r - c) > 1)
                distMat(r, c) = betaVec(max(r, c) - 1) - betaVec(min(r, c));
            end
        end
        %waitbar(r / cardinality, distMatWaitbar);
    end
    %delete(distMatWaitbar);
    
    % Convert each vector to a SAX representation
    newD = floor(newD);
    
    SAXData = zeros(N, newD * numChannels);
    %SAXDataWaitbar = waitbar(0, 'Converting to SAX');
    for n = 1:N
        for c = 1:numChannels
            dataVector = data(n, (c - 1) * D + 1:c * D);
            
            % Normalize the vector prior to conversion
            dataVector = dataVector - mean(dataVector);
            dataStd = std(dataVector);
            if (dataStd > 1)
                dataVector = dataVector / dataStd;
            end
            
            for letter = 1:newD
                subsequence = dataVector((letter - 1) * letterLength + 1:letter * letterLength);
                SAXData(n, (c - 1) * newD + letter) = convertSubsequenceToSAX(subsequence, betaVec, cardinality);
            end
        end
        %waitbar(n / N, SAXDataWaitbar);
    end
    %delete(SAXDataWaitbar);
end

function letter = convertSubsequenceToSAX(subsequence, betaVec, cardinality)
    PAA = mean(subsequence);
    letter = 1;
    for i = 1:length(betaVec)
        if (PAA > betaVec(i))
            letter = letter + 1;
        else
            break;
        end
    end

    if (letter > cardinality || letter < 1)
        error('This should never happen!');
    end
end