function minDistance = CalcMinDistanceSAX(sample, candidate, channel, ...
    numChannels, distMat)
    % Find the minimum distance between the sample and the candidate
    channelLength = length(sample) / numChannels;
    sample = sample((channel - 1) * channelLength + 1:channel * channelLength);
    
    candidateLength = length(candidate);
    minDistance = -1;
    
    % Slide candidates across the sample to find the min distance
    % Quit early if this is greater than minDistance
    for start = 1:(channelLength - candidateLength + 1)
        curDistance = 0;
        foundMin = 1;
        
        for index = 1:candidateLength
            curDistance = curDistance + distMat(sample(start + index - 1), candidate(index));
            if (minDistance > 0 && curDistance > minDistance)
                % We know we already got past min so quit here
                foundMin = 0;
                break;
            end
        end
        
        if (foundMin)
            minDistance = curDistance;
        end
    end
end