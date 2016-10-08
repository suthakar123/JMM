% TABLE OF CONTENTS:
%
%  #1 - AnalyzeFeaturesGUI:
%           Necessary initialiazation code for the GUI
%  #2 - AnalyzeFeaturesGUI_OpeningFcn:
%           Executes just before AnalyzeGUI is made visible
%  #3 - AnalyzeFeaturesGUI_OutputFcn:
%           Outputs from this function are returned to the command line
%  #4 - ActivityPopUp_Callback:
%           Executes when the Activity pop up is pressed
%  #5 - ActivityPopUp_CreateFcn:
%           Adjusts the ActivityPopUp colors for Windows machines

% ---  #1 --- %
function varargout = AnalyzeFeaturesGUI(varargin)
    % ANALYZEFEATURESGUI MATLAB code for AnalyzeFeaturesGUI.fig
    %      ANALYZEFEATURESGUI, by itself, creates a new ANALYZEFEATURESGUI or raises the existing
    %      singleton*.
    %
    %      H = ANALYZEFEATURESGUI returns the handle to a new ANALYZEFEATURESGUI or the handle to
    %      the existing singleton*.
    %
    %      ANALYZEFEATURESGUI('CALLBACK',hObject,eventData,handles,...) calls the local
    %      function named CALLBACK in ANALYZEFEATURESGUI.M with the given input arguments.
    %
    %      ANALYZEFEATURESGUI('Property','Value',...) creates a new ANALYZEFEATURESGUI or raises the
    %      existing singleton*.  Starting from the left, property value pairs are
    %      applied to the GUI before AnalyzeFeaturesGUI_OpeningFcn gets called.  An
    %      unrecognized property name or invalid value makes property application
    %      stop.  All inputs are passed to AnalyzeFeaturesGUI_OpeningFcn via varargin.
    %
    %      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
    %      instance to run (singleton)".
    %
    % See also: GUIDE, GUIDATA, GUIHANDLES

    % Edit the above text to modify the response to help AnalyzeFeaturesGUI

    % Last Modified by GUIDE v2.5 24-Dec-2013 13:16:24

    % Begin initialization code - DO NOT EDIT
    gui_Singleton = 1;
    gui_State = struct('gui_Name',       mfilename, ...
                       'gui_Singleton',  gui_Singleton, ...
                       'gui_OpeningFcn', @AnalyzeFeaturesGUI_OpeningFcn, ...
                       'gui_OutputFcn',  @AnalyzeFeaturesGUI_OutputFcn, ...
                       'gui_LayoutFcn',  [] , ...
                       'gui_Callback',   []);
    if nargin && ischar(varargin{1})
        gui_State.gui_Callback = str2func(varargin{1});
    end

    if nargout
        [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
    else
        gui_mainfcn(gui_State, varargin{:});
    end
    % End initialization code - DO NOT EDIT
end

% ---  #2 --- %
function AnalyzeFeaturesGUI_OpeningFcn(hObject, ~, handles, varargin)
    % This function has no output args, see OutputFcn.
    % hObject    handle to figure
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    structure with handles and user data (see GUIDATA)
    % varargin   command line arguments to AnalyzeFeaturesGUI (see VARARGIN)

    % Choose default command line output for AnalyzeFeaturesGUI
    handles.output = hObject;

    % Load the feature matrix that should have been created in LearningGUI
    featureMatrix = getappdata(0, 'analyzeFeatureMatrix');
    
    if (isempty(featureMatrix))
        % If there was no feature matrix, load data from the specified file
        testData = importdata('analyzeFeaturesTest.mat');
        featureMatrix = testData.featureMatrix;
        classificationVector = testData.classificationVector;
        activitiesUsed = testData.activitiesUsed;
        featureArray = testData.featureArray;
        channelNames = testData.channelNames;
        originVector = testData.originVector;
    else
        classificationVector = getappdata(0, 'analyzeClassificationVector');
        activitiesUsed = getappdata(0, 'analyzeActivitiesUsed');
        featureArray = getappdata(0, 'analyzeFeatureArray');
        channelNames = getappdata(0, 'analyzeChannelNames');
        originVector = getappdata(0, 'analyzeOriginVector');
    end
    
    % Set up the ActivityPopUp values
    activityValues = cell(1, length(activitiesUsed) + 1);
    activityValues{1} = 'Activity Name';
    for activity = 1:length(activitiesUsed)
        activityValues{activity + 1} = activitiesUsed{activity};
    end
    
    set(handles.ActivityPopUp, 'String', activityValues);
    
    handles.featureMatrix = featureMatrix;
    handles.classificationVector = classificationVector;
    handles.activitiesUsed = activitiesUsed;
    handles.featureArray = featureArray;
    handles.channelNames = channelNames;
    handles.originVector = originVector;
    
    % Update handles structure
    guidata(hObject, handles);

    % UIWAIT makes AnalyzeFeaturesGUI wait for user response (see UIRESUME)
    % uiwait(handles.figure1);
end

% ---  #3 --- %
function varargout = AnalyzeFeaturesGUI_OutputFcn(~, ~, handles) 
    % varargout  cell array for returning output args (see VARARGOUT);
    % hObject    handle to figure
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    structure with handles and user data (see GUIDATA)

    % Get default command line output from handles structure
    varargout{1} = handles.output;
end

% ---  #4 --- %
function ActivityPopUp_Callback(hObject, ~, handles) %#ok<DEFNU>
    % hObject    handle to ActivityPopUp (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    structure with handles and user data (see GUIDATA)

    % Hints: contents = cellstr(get(hObject,'String')) returns ActivityPopUp contents as cell array
    %        contents{get(hObject,'Value')} returns selected item from ActivityPopUp
    
    % Get the selected activity
    selectedActivityIndex = get(hObject, 'Value') - 1;
    if (selectedActivityIndex > 0)
        
        % Find all of the feature values for this activity
        selectedIndices = ismember(handles.classificationVector, handles.activitiesUsed{selectedActivityIndex});
        
        % Filter all of the found activity feature values
        featureColumns = size(handles.featureMatrix, 2);
        numInstances = sum(selectedIndices);
        activityFeatures = zeros(numInstances, featureColumns);
        activityOrigins = cell(numInstances, 1);
        foundRows = 0;
        for row = 1:length(handles.classificationVector)
            if (selectedIndices(row))
                foundRows = foundRows + 1;
                activityFeatures(foundRows, :) = handles.featureMatrix(row, :);
                activityOrigins(foundRows) = handles.originVector(row);
            end
        end
        
        % Make a new figure and plot all of the feature values as histograms
        figure('Name', handles.activitiesUsed{selectedActivityIndex});
        
        % Generate the row and column labels
        numrows = length(handles.channelNames) * 2;
        rowlabels = cell(1, numrows);
        for row = 1:numrows
            if (row > numrows / 2)
                channel = handles.channelNames{row - numrows / 2};
                rowlabels{row} = [channel, ' Derivative'];
            else
                channel = handles.channelNames{row};
                rowlabels{row} = channel;
            end
        end
            
        numcols = length(handles.featureArray);
        collabels = cell(1, numcols);
        for col = 1:numcols
            collabels{col} = handles.featureArray{col};
        end
        
        if (strcmp(handles.activitiesUsed{selectedActivityIndex}, 'Standing'))
            for row = 1:length(activityOrigins)
                if (activityFeatures(row, 22) > 0.0)
                    disp(activityOrigins(row));
                end
            end
        end
        
        % Keep track of the instances outside of the 3 sd range
        outlierCounts = zeros(1, numInstances);
        
        % Generate each plot
        for row = 1:numrows
            for col = 1:numcols
                histIndex = (row - 1) * numcols + col;
                subplot(numrows, numcols, histIndex);
                hist(activityFeatures(:, histIndex));
                
                % Show the row label if this is the first column
                if (col == 1)
                    ylabel(rowlabels{row});
                end
                
                % Show the column label if this is the first row
                if (row == 1)
                    title(collabels{col});
                end
                
                % Add 2 lines to show +/- 3 sds
                dataMean = mean(activityFeatures(:, histIndex));
                dataSd = std(activityFeatures(:, histIndex));
                ydimensions = ylim;
                
                dataMin = dataMean - 3 * dataSd;
                dataMax = dataMean + 3 * dataSd;
                
                line('XData', [dataMin, dataMin], ...
                    'YData', [0, ydimensions(2)], 'Color', [1.0, 0.0, 0.0]);
                line('XData', [dataMax, dataMax], ...
                    'YData', [0, ydimensions(2)], 'Color', [1.0, 0.0, 0.0]);
                
                % Record all of the outliers
                for instance = 1:numInstances
                    if (activityFeatures(instance, histIndex) < dataMin || ...
                            activityFeatures(instance, histIndex) > dataMax)
                        outlierCounts(instance) = outlierCounts(instance) + 1;
                    end
                end
            end
        end
        
        % Create a separate figure for the outliers
        figure('Name', ['Outlier counts for ', handles.activitiesUsed{selectedActivityIndex}]);
        hist(outlierCounts, max(outlierCounts) + 1);
        xlabel('# of outlier features');
        ylabel('# of instances');
        
        % Print out how many instances will be removed with the given
        % outlier threshold
        outlierThreshold = 1;
        numPruned = sum(outlierCounts > outlierThreshold);
        disp([num2str(numPruned), ' instances pruned for ', ...
            handles.activitiesUsed{selectedActivityIndex}, ' (', ...
            num2str(numPruned / numInstances * 100), '% of activities)']);
    end
end

% ---  #5 --- %
function ActivityPopUp_CreateFcn(hObject, ~, ~) %#ok<DEFNU>
    % hObject    handle to ActivityPopUp (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    empty - handles not created until after all CreateFcns called

    % Hint: popupmenu controls usually have a white background on Windows.
    %       See ISPC and COMPUTER.
    if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
        set(hObject,'BackgroundColor','white');
    end
end