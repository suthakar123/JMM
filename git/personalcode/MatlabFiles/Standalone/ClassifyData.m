% TABLE OF CONTENTS:
%
%  #1 - ClassifyData:
%           This function classifies the passed in data matrix using the
%           tree corresponding to the passed in TreeFileName(which is basically 
%           the path to the Tree. Results of the classification are returned 
%           as durations for each activity.Sampling rate,window width,SAXWindowWidth and SAXCardinality
%           are obtained from the database calls made by Java.
%  #2 - LoadTree:
%           Function responsible for loading in the classification tree and
%           the relevant classification attributes.
%  #3 - FindTreeData:---------NOT BEING USED NOW.
%           Validates the passed in tree ID to make sure that such a tree
%           actually exists. Once the tree ID is validated, the sampling
%           rate, window width and the file path to the classification tree
%           are loaded in
%  #4 - LoadTreeFromFile:
%           Loads the tree from the file found in FindTreeData
%  #5 - ClassifyDataMatrix:
%           Classifies the data matrix based on the passed in
%           classification tree, window width and sampling rate
%  #6 - ClassifyValidatedDataMatrix:
%           Performs the classification but assumes that all of the passed
%           in arguments have already been validated in ClassifyDataMatrix
%  #7 - CalculateFeatures:
%           Calculates the features on a given data matrix and returns a
%           feature matrix
%  #8 - OutputResults:
%           Parses the results and classification results and returns the
%           duration of each activity in hours

% ---  #1 --- %
function [Err, ActivityDurations] = ClassifyData(TreeFileName,TreeDataFromDataBase, DataMatrix)
    % TreeFileName          Contains the Path of the classification tree
    %                       where classification tree is placed. This path 
    %                       has been picked from the database.
    % TreeDataFromDataBase  Contains the information from the database
    %                       which are required for Classification.The data
    %                       that it holds are samplingRate, windowSize, saxWindowWidth,
    %                       saxCardinality.
    % DataMatrix            A matrix that holds the data to be classified. Each row
    %                       is one timestep of data and the number of columns is
    %                       equal to the number of channels used for the
    %                       classification tree
    
    ActivityDurations = [];
    SamplingRate = TreeDataFromDataBase(1);
    WindowWidth = TreeDataFromDataBase(2);
    SAXWindowWidth = TreeDataFromDataBase(3);
    SAXCardinality = TreeDataFromDataBase(4);
    %fprintf('Sampling Rate %g\n',SamplingRate);
    %fprintf('Window width %g\n',WindowWidth);
    %fprintf('SAXWindowWidth %g\n',SAXWindowWidth);
    %fprintf('SAXCardinality %g\n',SAXCardinality);
    % First thing we want to do is to load the tree
    [Err, ClassificationTree, ActivitiesUsed] = LoadTree(TreeFileName);
    
    % Make sure that there were no errors and quit if there were
    if (Err ~= 0)
        return;
    end
    
    % Next we want to classify the data using the loaded tree
    [Err, ClassificationVector] = ClassifyDataMatrix(ClassificationTree, ActivitiesUsed, ...
        DataMatrix, SamplingRate, WindowWidth, SAXWindowWidth, SAXCardinality);
    
    % Make sure that there were no errors and quit if there were
    if (Err ~= 0)
        return;
    end
    
    % Finally write the results to the database
    fprintf('Post-processing results...');
    [Err, ActivityDurations] = OutputResults(ClassificationVector, WindowWidth);
    fprintf('Done\n');
end

% ---  #2 --- %
function [Err, ClassificationTree, ActivitiesUsed] = LoadTree(TreeFileName)
    % TreeFileName  The ID of the classification tree to be loaded from the
    %               database
    
    % First we need to load all of the data for the given tree
    %fprintf('Accessing tree data...');
    %[Err, treeFileName, SamplingRate, WindowWidth, SAXWindowWidth, ...
    %   SAXCardinality] = FindTreeData(TreeID);
    %fprintf('Done\n');
    
    % Here we are guaranteed to have a valid tree so we can load it in
    fprintf('Loading tree file...');
    [Err, ClassificationTree, ActivitiesUsed] = LoadTreeFromFile(TreeFileName);
    fprintf('Done\n');
end

% ---  #3 --- %--NOT BEING USED FOR NOW
% function [Err, TreeFileName, SamplingRate, WindowWidth, SAXWindowWidth, ...
%     SAXCardinality] = FindTreeData(TreeID)
%     % TreeID        The ID of the classification tree to be loaded from the
%     %               database
% 
%     TreeFileName = '';
%     SamplingRate = 0;
%     WindowWidth = 0;
%     SAXWindowWidth = 0;
%     SAXCardinality = 0;
%     Err = 0;
%     
%     % Access the database to get all of this information
%     try
%         %conn = database('CRA_JMM','','','Vendor','Microsoft SQL Server','Server','JMM-DB-DEV','AuthType','Windows','PortNumber',1433);
%     conn = database('CRA_JMM','','','Vendor','Microsoft SQL Server','Server','U-OSM-1265797\\CAVANAGHLAB2008','AuthType','Windows','PortNumber',56485);
%     catch errorMessage
%         disp(errorMessage);
%         Err = 1;
%         return;
%     end
%     
%     % Check to make sure the connection was successful
%     if (isa(conn, 'database') == 0 || conn.Handle == 0)
%         Err = 1;
%         return;
%     end
%     
%     try
%         % Execute the query to get all of the tree data
%         %display(TreeID);
%         query = ['select filePath, samplingRate, windowSize,saxWindowWidth,saxCardinality ', ...
%             ' from Tree where treeId=''',TreeID,''''];
%         result = exec(conn, query);
%         %display(result);
%         if (isa(result, 'cursor') == 0)
%             Err = 2;
%             close(conn);
%             return;
%         end
%         
%         resultData = fetch(result);
%         
%         close(conn);
%         
%         if (isa(resultData, 'cursor') == 0)
%             Err = 2;
%             return;
%         end
%         
%         resultData = resultData.Data;
%     catch errorMessage
%         disp(errorMessage);
%         Err = 2;
%         return;
%     end
%         %display(resultData);
%     % Make sure the result only has 5 entries
%     if (isa(resultData, 'cell') == 0 || size(resultData, 1) ~= 1 || size(resultData, 2) ~= 5)
%         Err = 3;
%       %  display('in first');
%        display(size(resultData,2));
%         return;
%     end
%     
%     % Get all of the data in the proper format - everything is a cell array
%     % when the query result is first returned
%     try
%         TreeFileName = char(resultData(1));
%         display(TreeFileName);
%         SamplingRate = resultData(2);
%         SamplingRate = SamplingRate{1};
%         WindowWidth = resultData(3);
%         WindowWidth = WindowWidth{1};
%         SAXWindowWidth = resultData(4);
%         SAXWindowWidth = SAXWindowWidth{1};
%         SAXCardinality = resultData(5);
%         SAXCardinality = SAXCardinality{1};
%     catch errorMessage
%         disp(errorMessage);
%         Err = 3;
%         return;
%     end
% end

% ---  #4 --- %
function [Err, ClassificationTree, ActivitiesUsed] = LoadTreeFromFile(TreeFileName)
    % TreeFileName  The name and path to the file where the tree file name
    %               is stored

    ClassificationTree = {};
    ActivitiesUsed = {};
    
    % Try importing the tree and return an error if it doesn't work
    try
       %disp('Before importing');
        TreeData = importdata(TreeFileName);
       %disp('After importing');
       % disp(TreeData);
        ClassificationTree = TreeData.classificationTree;
        ActivitiesUsed = TreeData.activitiesUsed;
    catch errorMessage
        disp(errorMessage);
        Err = 4;
        return;
    end
    
    Err = 0;
end

% ---  #5 --- %
function [Err, ClassificationVector] = ClassifyDataMatrix(ClassificationTree, ...
    ActivitiesUsed, DataMatrix, SamplingRate, WindowWidth, SAXWindowWidth, SAXCardinality)
    % ClassificationTree    A classification tree to use for classification
    % ActivitiesUsed        The activities the classification tree was
    %                       trained from
    % DataMatrix            A matrix that holds the data to be classified. 
    %                       Each row is one timestep of data and the number 
    %                       of columns is equal to the number of channels 
    %                       used for the classification tree
    % SamplingRate          The sampling rate of the data
    % WindowWidth           The window width to use for classification
    % SAXWindowWidth        The window width for SAX conversion
    % SAXCardinality        The cardinality for SAX conversion
    
    ClassificationVector = {};
    
    % Hard-code the number of channels used for classification in order to
    % check the passed in data matrix
    numOfChannels = 5;
    
    fprintf('Checking parameters...');
    if (size(DataMatrix, 2) ~= numOfChannels || size(DataMatrix, 1) <= 0)
        Err = 6;
        return;
    end
    
    % Verify the rest of the arguments
    if (isa(ClassificationTree, 'TreeNode') == 0 && isa(ClassificationTree, 'TreeNodeShapelet') == 0)
        Err = 5;
        return;
    end
    if (isempty(SamplingRate) || isnumeric(SamplingRate) == 0 || SamplingRate <= 0)
        Err = 7;
        return;
    end
    if (isempty(WindowWidth) || isnumeric(WindowWidth) == 0 || WindowWidth <= 0)
        Err = 8;
        return;
    end
    if (isempty(SAXWindowWidth) || isnumeric(SAXWindowWidth) == 0 || SAXWindowWidth <= 0)
        Err = 8;
        return;
    end
    if (isempty(SAXCardinality) || isnumeric(SAXCardinality) == 0 || SAXCardinality <= 0)
        Err = 8;
        return;
    end
    fprintf('Done\n');
    
    % Convert the sampling rate and window width to integers now that they
    % are verified to be numbers. This is to ensure that there are no
    % issues with indexing
    SamplingRate = int32(SamplingRate);
    WindowWidth = int32(WindowWidth);
    SAXWindowWidth = int32(SAXWindowWidth);
    SAXCardinality = int32(SAXCardinality);
    
    % Now that the arguments have been validated, we can classify the data
    [Err, ClassificationVector] = ClassifyValidatedDataMatrix(ClassificationTree, ...
        ActivitiesUsed, DataMatrix, SamplingRate, WindowWidth, SAXWindowWidth, SAXCardinality);
end

% ---  #6 --- %
function [Err, ClassificationVector] = ClassifyValidatedDataMatrix(ClassificationTree, ...
    ActivitiesUsed, DataMatrix, SamplingRate, WindowWidth, SAXWindowWidth, SAXCardinality)
    % ClassificationTree    A classification tree to use for classification
    % ActivitiesUsed        The activities the classification tree was
    %                       trained from
    % DataMatrix            A matrix that holds the data to be classified. 
    %                       Each row is one timestep of data and the number 
    %                       of columns is equal to the number of channels 
    %                       used for the classification tree
    % SamplingRate          The sampling rate of the data
    % WindowWidth           The window width to use for classification
    
    % First we need to calculate features on this data
    initTimeForCreateFeature=cputime;
    fprintf('Calculating features...');
    [Err, ShapeletMatrix, FeatureMatrix] = CreateFeatureDataMatrixNoDiff(DataMatrix, ...
        WindowWidth, SamplingRate);
    
    fprintf('Done\n');
    finTimeForCreateFeature=cputime;
    fprintf('Time to createFeatureDataMatrix %g\n',finTimeForCreateFeature-initTimeForCreateFeature);
    
    if (Err ~= 0)
        % Simply pass the error up and stop execution
        ClassificationVector = {};
        return;
    end
    
    try
        % We have to convert the shapelet matrix into SAX
        initTime = cputime;
        fprintf('Transforming to SAX...');
        
        [SAXMatrix, ~, distMat] = TransformToSAX(ShapeletMatrix, ...
            5, SAXWindowWidth, double(SAXCardinality));
        finTimeForSax = cputime;
        fprintf('Done\n');
     %   fprintf('Time for Transform SAX %g\n', finTimeForSax-initTime);
        
        % Use the predict function of the classification tree
        initTimeForClassify = cputime;
        fprintf('Classifying activties...');
        ClassificationVector = classifyDataShapelet(ClassificationTree, ...
            FeatureMatrix, SAXMatrix, ActivitiesUsed, 5, 0, 0, [], distMat);
        fprintf('Done\n');
        finTimeForClassify=cputime;
      %  fprintf('Time to Classify Data Shapelet %g\n', finTimeForClassify-initTimeForClassify);
        
    catch errorMessage
        disp(errorMessage.message);
        Err = 13;
        ClassificationVector = {};
        return;
    end
        
    Err = 0;
end

% ---  #7 --- %
function [Err, ShapeletMatrix, FeatureMatrix] = CreateFeatureDataMatrix(DataMatrix, ...
    WindowWidth, SamplingRate)

    % This value represents how many samples of data will be used to
    % calculate each of the features below. For now, this value will be
    % calculated manually here but it would be very useful to expose this
    % to the user and add lines on the graph to show each split
    featureArray = {'max';'min';'range';'mean';'std';'calcFFT';'FFT_mod';'skewness';'kurtosis'};
    sampleSize = SamplingRate * WindowWidth;
    numOfGroups = floor(size(DataMatrix, 1) / double(sampleSize));
    numChannels = size(DataMatrix, 2);
    numOfFeatures = length(featureArray);
    
    try
        ShapeletMatrix = zeros(numOfGroups, sampleSize * numChannels);
        FeatureMatrix = zeros(numOfGroups, 2 * numOfFeatures * numChannels);
    catch errorMessage
        % Report an error if there is not enough memory for the feature
        % matrix
        disp(errorMessage);
        Err = 10;
        FeatureMatrix = [];
        ShapeletMatrix = [];
        return;
    end
    
    for groupNumber = 1:numOfGroups
        % Get the subset of the data that we will be working on 
        dataGroup = DataMatrix((sampleSize*(groupNumber - 1) + 1):sampleSize*groupNumber,:);
        
        for channel = 1:numChannels
            ShapeletMatrix(groupNumber, ((channel - 1) * sampleSize + 1): ...
                (channel * sampleSize)) = dataGroup(:, channel);
        
            % Calculate features for regular data
            dataVector = dataGroup(:,channel);
            featureCount = 1;
            for feature = 1:length(featureArray)
                try
                    FeatureMatrix(groupNumber, numOfFeatures * (channel - 1)...
                        + featureCount) = eval([char(featureArray(feature)),'(dataVector)']);
                catch errorMessage
                    % Report an error during feature calculation
                    disp(errorMessage);
                    Err = 9;
                    return;
                end
                featureCount = featureCount + 1;
            end

            % Calculate features for derivative
            diffDataVector = diff(dataVector); %#ok<NASGU> Used in eval call below
            featureCount = 1;
            for feature = 1:length(featureArray)
                try
                    FeatureMatrix(groupNumber, numOfFeatures * (numChannels + channel - 1)...
                        + featureCount) = eval([char(featureArray(feature)),'(diffDataVector)']);
                catch errorMessage
                    % Report an error during feature calculation
                    disp(errorMessage);
                    Err = 9;
                    return;
                end
                featureCount = featureCount + 1;
            end
        end
    end
    
    Err = 0;
end

% ---  #8 --- %
function [Err,ActivityDurations] = OutputResults(ClassificationVector, WindowWidth)
    % ClassificationVector      A cell array that contains the names of all
    %                           of the activities that the subject did
    
    ActivityDurations = [];
    
    % First we need to make sure that Classification Vector is a non-empty
    % cell array
    if (isa(ClassificationVector, 'cell') == 0 || length(ClassificationVector) <= 0)
        Err = 14;
        return;
    end
    
    % Convert the activities to their proper names if we have that issue
    for activity = 1:length(ClassificationVector)
        if (strcmp('Knee_extensions', ClassificationVector(activity)))
            ClassificationVector(activity) = cellstr('Exercise');
        elseif (strcmp('Cycling', ClassificationVector(activity)))
            ClassificationVector(activity) = cellstr('Bike');
        elseif (strcmp('Sitting_table', ClassificationVector(activity)))
            ClassificationVector(activity) = cellstr('Sitting');
        elseif (strcmp('Sitting_recliner', ClassificationVector(activity)))
            ClassificationVector(activity) = cellstr('Sitting');
        elseif (strcmp('Stairs_down', ClassificationVector(activity)))
            ClassificationVector(activity) = cellstr('Stairs');
        elseif (strcmp('Stairs_up', ClassificationVector(activity)))
            ClassificationVector(activity) = cellstr('Stairs');
        end
    end
    
    expectedActivities = {'Lying', 'Sitting', 'Standing', 'Walking', 'Stairs', ...
        'Exercise', 'Bike'};
    ActivityDurations = zeros(1, length(expectedActivities));
    
    for activity = 1:length(ClassificationVector)
        index = find(strcmp(expectedActivities, ClassificationVector(activity)));
        if (isempty(index))
            % Report an error if we can't find the
            %disp(activity);
            if (strcmp(ClassificationVector(activity),'Unknown') == 0)
                disp(ClassificationVector(activity));
                Err = 15;
                return;
            end
        else
            ActivityDurations(index) = ActivityDurations(index) + 1;
        end
    end
    
    for index = 1:length(ActivityDurations)
        % Now we need to scale the result to make it in terms of hours
        ActivityDurations(index) = ActivityDurations(index) * double(WindowWidth) / 3600.0;
    end
    
    Err = 0;
end



function [Err, ShapeletMatrix, FeatureMatrix] = CreateFeatureDataMatrixNoDiff(DataMatrix, ...
    WindowWidth, SamplingRate)


    % This value represents how many samples of data will be used to
    % calculate each of the features below. For now, this value will be
    % calculated manually here but it would be very useful to expose this
    % to the user and add lines on the graph to show each split
    featureArray = {'max';'min';'range';'mean';'std';'calcFFT';'FFT_mod';'skewness';'kurtosis'};
    sampleSize = SamplingRate * WindowWidth;
    numOfGroups = floor(size(DataMatrix, 1) / double(sampleSize));
    numChannels = size(DataMatrix, 2);
    numOfFeatures = length(featureArray);
    
    try
        ShapeletMatrix = zeros(numOfGroups, sampleSize * numChannels);
        FeatureMatrix = zeros(numOfGroups,  numOfFeatures * numChannels);
    catch errorMessage
        % Report an error if there is not enough memory for the feature
        % matrix
        disp(errorMessage);
        Err = 10;
        FeatureMatrix = [];
        ShapeletMatrix = [];
        return;
    end
    
    for groupNumber = 1:numOfGroups
        % Get the subset of the data that we will be working on 
        dataGroup = DataMatrix((sampleSize*(groupNumber - 1) + 1):sampleSize*groupNumber,:);
        
        for channel = 1:numChannels
            ShapeletMatrix(groupNumber, ((channel - 1) * sampleSize + 1): ...
                (channel * sampleSize)) = dataGroup(:, channel);
        
            % Calculate features for regular data
            dataVector = dataGroup(:,channel);
            featureCount = 1;
            for feature = 1:length(featureArray)
                try
                    FeatureMatrix(groupNumber, numOfFeatures * (channel - 1)...
                        + featureCount) = eval([char(featureArray(feature)),'(dataVector)']);
                catch errorMessage
                    % Report an error during feature calculation
                    disp(errorMessage);
                    Err = 9;
                    return;
                end
                featureCount = featureCount + 1;
            end

            
            
        end
    end
    
    Err = 0;
end