% TABLE OF CONTENTS:
%
%  #1 - ClassifyGUI:
%           Necessary initialiazation code for the GUI
%  #2 - ClassifyGUI_OpeningFcn:
%           Executes just before ClassifyGUI is made visible
%  #3 - ClassifyGUI_OutputFcn:
%           Outputs from this function are returned to the command line
%  #4 - SubjectsListbox_CreateFcn:
%           Adjusts the SubjectsListbox colors for Windows machines
%  #5 - ActivitesListbox_CreateFcn:
%           Adjusts the ActivitiesListbox colors for Windows machines
%  #6 - AllSubjectsButton_Callback:
%           Selects all of the subjects
%  #7 - RandomSubjectsButton_Callback:
%           Selects a random group of subjects
%  #8 - AllActivitiesButton_Callback:
%           Selects all of the activities
%  #9 - SubmitButton_Callback:
%           Records the selected subjects and activities and then closes
%           the GUI
% #10 - SelectAllInListbox:
%           Helper function to select all the items in the given listbox
% #11 - FeaturesListbox_CreateFcn:
%           Adjusts the FeaturesListbox colors for Windows machines
% #12 - AllFeaturesButton_Callback:
%           Selects all of the features
% #13 - TreeDepthPopup_CreateFcn:
%           Adjusts the TreeDepthPopup colors for Windows machines
% #14 - ThresholdPopup_CreateFcn:
%           Adjusts the ThresholdPopup colors for Windows machines
% #15 - CrossValidationCheckbox_Callback:
%           Disables the TreeDepth and ThresholdValue popups if the cross
%           validation checkbox is checked
% #16 - SAXWindowWidthPopup_CreateFcn:
%           Adjusts the SAXWindowWidthPopup colors for Windows machines
% #17 - SAXCardinalityPopup_CreateFcn:
%           Adjusts the SAXCardinalityPopup colors for Windows machines
% #18 - NumCandidatesClassPopup_CreateFcn:
%           Adjusts the NumCandidatesClassPopup colors for Windows machines
% #19 - MinShapeletTextbox_CreateFcn:
%           Adjusts the MinShapeletTextbox colors for Windows machines
% #20 - MaxShapeletTextbox_CreateFcn:
%           Adjusts the MaxShapeletTextbox colors for Windows machines

% ---  #1 --- %
function varargout = ClassifyGUI(varargin)
    % CLASSIFYGUI MATLAB code for ClassifyGUI.fig
    %      CLASSIFYGUI, by itself, creates a new CLASSIFYGUI or raises the existing
    %      singleton*.
    %
    %      H = CLASSIFYGUI returns the handle to a new CLASSIFYGUI or the handle to
    %      the existing singleton*.
    %
    %      CLASSIFYGUI('CALLBACK',hObject,eventData,handles,...) calls the local
    %      function named CALLBACK in CLASSIFYGUI.M with the given input arguments.
    %
    %      CLASSIFYGUI('Property','Value',...) creates a new CLASSIFYGUI or raises the
    %      existing singleton*.  Starting from the left, property value pairs are
    %      applied to the GUI before ClassifyGUI_OpeningFcn gets called.  An
    %      unrecognized property name or invalid value makes property application
    %      stop.  All inputs are passed to ClassifyGUI_OpeningFcn via varargin.
    %
    %      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
    %      instance to run (singleton)".
    %
    % See also: GUIDE, GUIDATA, GUIHANDLES

    % Edit the above text to modify the response to help ClassifyGUI

    % Last Modified by GUIDE v2.5 07-Apr-2014 00:03:41

    % Begin initialization code - DO NOT EDIT
    gui_Singleton = 1;
    gui_State = struct('gui_Name',       mfilename, ...
                       'gui_Singleton',  gui_Singleton, ...
                       'gui_OpeningFcn', @ClassifyGUI_OpeningFcn, ...
                       'gui_OutputFcn',  @ClassifyGUI_OutputFcn, ...
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
function ClassifyGUI_OpeningFcn(hObject, ~, handles, varargin)
    % This function has no output args, see OutputFcn.
    % hObject    handle to figure
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    structure with handles and user data (see GUIDATA)
    % varargin   command line arguments to ClassifyGUI (see VARARGIN)

    % Choose default command line output for ClassifyGUI
    handles.output = hObject;

    % Initialize the listboxes based on the passed in values
    subjectIndices = getappdata(0, 'selectableSubjects');
    subjectsCellArray = cell(1, length(subjectIndices));
    for subject = 1:length(subjectIndices)
        subjectsCellArray{subject} = ['Subject ', ...
            num2str(subjectIndices(subject))];
    end
    set(handles.SubjectsListbox, 'String', subjectsCellArray);
    set(handles.ActivitiesListbox, 'String', getappdata(0, 'selectableActivities'));
    set(handles.FeaturesListbox, 'String', getappdata(0, 'selectableFeatures'));
    handles.SAXWindowWidthData = {'Select one:', '1', '2', '4', '8', '16'};
    set(handles.SAXWindowWidthPopup, 'String', handles.SAXWindowWidthData);
    handles.SAXCardinalityData = {'Select one:', '11', '21', '51', '101', '201', '501'};
    set(handles.SAXCardinalityPopup, 'String', handles.SAXCardinalityData);
    handles.NumCandidatesClassData = {'Select one:', '2', '5', '10'};
    set(handles.NumCandidatesClassPopup, 'String', handles.NumCandidatesClassData);
    
    % If we are in testing mode, we only want subjects and activites to be
    % selectable
    if (getappdata(0, 'testingMode') == 1)
        set(handles.FeaturesListbox, 'Enable', 'off');
        set(handles.AllFeaturesButton, 'Enable', 'off');
        set(handles.TreeDepthPopup, 'Enable', 'off');
        set(handles.ThresholdPopup, 'Enable', 'off');
        set(handles.CrossValidationCheckbox, 'Enable', 'off');
        set(handles.SAXWindowWidthPopup, 'Enable', 'off');
        set(handles.SAXCardinalityPopup, 'Enable', 'off');
        set(handles.NumCandidatesClassPopup, 'Enable', 'off');
        set(handles.MinShapeletTextbox, 'Enable', 'off');
        set(handles.MaxShapeletTextbox, 'Enable', 'off');
        SelectAllInListbox(handles.FeaturesListbox);
        
        % We should also display the tree depth and the classification
        % threshold along with whether the tree was trained using cross
        % validation
        set(handles.CrossValidationCheckbox, 'Value', getappdata(0, 'crossValidationEnabled'));
        treeDepth = getappdata(0, 'treeDepth');
        [~, treeDepthIndex] = max(ismember(get(handles.TreeDepthPopup, 'String'), ...
            num2str(treeDepth)));
        set(handles.TreeDepthPopup, 'Value', treeDepthIndex);
        thresholdValue = getappdata(0, 'classThresholdValue') * 100;
        [~, thresholdValueIndex] = max(ismember(get(handles.ThresholdPopup, 'String'), ...
            [num2str(thresholdValue), '%']));
        set(handles.ThresholdPopup, 'Value', thresholdValueIndex);
        
        set(handles.SAXWindowWidthPopup, 'String', num2str(getappdata(0, 'SAXWindowWidth')));
        set(handles.SAXCardinalityPopup, 'String', num2str(getappdata(0, 'SAXCardinality')));
        set(handles.NumCandidatesClassPopup, 'String', num2str(getappdata(0, 'numCandidatesClass')));
        set(handles.MinShapeletTextbox, 'String', num2str(getappdata(0, 'minShapelet')));
        set(handles.MaxShapeletTextbox, 'String', num2str(getappdata(0, 'maxShapelet')));
    end
    
    % Update handles structure
    guidata(hObject, handles);

    % UIWAIT makes ClassifyGUI wait for user response (see UIRESUME)
    % uiwait(handles.ClassifyFigure
end

% ---  #3 --- %
function varargout = ClassifyGUI_OutputFcn(~, ~, handles) 
    % varargout  cell array for returning output args (see VARARGOUT);
    % hObject    handle to figure
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    structure with handles and user data (see GUIDATA)

    % Get default command line output from handles structure
    varargout{1} = handles.output;
end

% ---  #4 --- %
function SubjectsListbox_CreateFcn(hObject, ~, ~) %#ok<DEFNU>
    % hObject    handle to SubjectsListbox (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    empty - handles not created until after all CreateFcns called

    % Hint: listbox controls usually have a white background on Windows.
    %       See ISPC and COMPUTER.
    if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
        set(hObject,'BackgroundColor','white');
    end
end

% ---  #5 --- %
function ActivitiesListbox_CreateFcn(hObject, ~, ~) %#ok<DEFNU>
    % hObject    handle to ActivitiesListbox (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    empty - handles not created until after all CreateFcns called

    % Hint: listbox controls usually have a white background on Windows.
    %       See ISPC and COMPUTER.
    if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
        set(hObject,'BackgroundColor','white');
    end
end

% ---  #6 --- %
function AllSubjectsButton_Callback(~, ~, handles) %#ok<DEFNU>
    % hObject    handle to AllSubjectsButton (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    structure with handles and user data (see GUIDATA)
    
    SelectAllInListbox(handles.SubjectsListbox);
end

% ---  #7 --- %
function RandomSubjectsButton_Callback(~, ~, handles) %#ok<DEFNU>
    % hObject    handle to RandomSubjectsButton (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    structure with handles and user data (see GUIDATA)
    
    % First find out how many entries we have in the listbox
    numSubjects = numel(get(handles.SubjectsListbox, 'String'));
    
    % Create a random bit vector of that size
    bitVector = round(rand(1, numSubjects));
    
    % Now mark the proper indices as selected
    indices = zeros(1, sum(bitVector));
    indicesIndex = 1;
    for index = 1:numSubjects
        if (bitVector(index) == 1)
            indices(indicesIndex) = index;
            indicesIndex = indicesIndex + 1;
        end
    end
    
    set(handles.SubjectsListbox, 'Value', indices);
end

% ---  #8 --- %
function AllActivitiesButton_Callback(~, ~, handles) %#ok<DEFNU>
    % hObject    handle to AllActivitiesButton (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    structure with handles and user data (see GUIDATA)
    
    SelectAllInListbox(handles.ActivitiesListbox);
end

% ---  #9 --- %
function SubmitButton_Callback(~, ~, handles) %#ok<DEFNU>
    % hObject    handle to SubmitButton (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    structure with handles and user data (see GUIDATA)
    
    % Check to make sure that either cross validation or stopping
    % conditions were selected
    if (get(handles.CrossValidationCheckbox,'Value') || (get(handles.TreeDepthPopup,'Value') > 1 ...
            && get(handles.ThresholdPopup,'Value') > 1))
        
        if (getappdata(0, 'testingMode') == 1)
            setappdata(0, 'selectedSubjects', get(handles.SubjectsListbox, 'Value'));
            setappdata(0, 'selectedActivities', get(handles.ActivitiesListbox, 'Value'));
            setappdata(0, 'selectedFeatures', get(handles.FeaturesListbox, 'Value'));
            
            close(handles.ClassifyFigure);
        else
            % Check to make sure that all of the shapelet parameters have been
            % properly selected

            SAXWindowWidth = get(handles.SAXWindowWidthPopup, 'Value');
            SAXCardinality = get(handles.SAXCardinalityPopup, 'Value');
            numCandidatesClass = get(handles.NumCandidatesClassPopup, 'Value');
            minShapelet = str2double(get(handles.MinShapeletTextbox, 'String'));
            maxShapelet = str2double(get(handles.MaxShapeletTextbox, 'String'));
            if (SAXWindowWidth > 1 && SAXCardinality > 1 && numCandidatesClass > 1 ...
                && ~isnan(minShapelet) && ~isnan(maxShapelet))

                % Return the selected indices to the parent GUI
                setappdata(0, 'selectedSubjects', get(handles.SubjectsListbox, 'Value'));
                setappdata(0, 'selectedActivities', get(handles.ActivitiesListbox, 'Value'));
                setappdata(0, 'selectedFeatures', get(handles.FeaturesListbox, 'Value'));
                setappdata(0, 'crossValidationEnabled', get(handles.CrossValidationCheckbox, 'Value'));
                setappdata(0, 'SAXWindowWidth', str2double(handles.SAXWindowWidthData{SAXWindowWidth}));
                setappdata(0, 'SAXCardinality', str2double(handles.SAXCardinalityData{SAXCardinality}));
                setappdata(0, 'numCandidatesClass', str2double(handles.NumCandidatesClassData{numCandidatesClass}));
                setappdata(0, 'minShapelet', minShapelet);
                setappdata(0, 'maxShapelet', maxShapelet);

                depthString = get(handles.TreeDepthPopup, 'String');
                thresholdString = get(handles.ThresholdPopup, 'String');

                if (~get(handles.CrossValidationCheckbox, 'Value'))
                    depthValue = str2double(depthString{get(handles.TreeDepthPopup, 'Value')});
                    thresholdValue = thresholdString{get(handles.ThresholdPopup, 'Value')};
                    % We need to remove the percent symbol before converting
                    thresholdValue = str2double(thresholdValue(1:length(thresholdValue) - 1)) / 100;
                    setappdata(0, 'treeDepth', depthValue);
                    setappdata(0, 'classThresholdValue', thresholdValue);
                end

                close(handles.ClassifyFigure);
            else
                errordlg('You must enter all shapelet paramters');
            end
        end
    else
        errordlg('You must select either stopping conditions or cross validation');
    end
end

% --- #10 --- %
function SelectAllInListbox(listboxHandle)
    % First find out how many entries we have in the listbox
    numItems = numel(get(listboxHandle, 'String'));
    
    % Now mark all of the indices as selected
    indices = zeros(1, numItems);
    for index = 1:numItems
        indices(index) = index;
    end
    
    set(listboxHandle, 'Value', indices);
end

% --- #11 --- %
function FeaturesListbox_CreateFcn(hObject, ~, ~) %#ok<DEFNU>
    % hObject    handle to FeaturesListbox (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    empty - handles not created until after all CreateFcns called

    % Hint: listbox controls usually have a white background on Windows.
    %       See ISPC and COMPUTER.
    if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
        set(hObject,'BackgroundColor','white');
    end
end

% --- #12 --- %
function AllFeaturesButton_Callback(~, ~, handles) %#ok<DEFNU>
    % hObject    handle to AllFeaturesButton (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    structure with handles and user data (see GUIDATA)
    
    SelectAllInListbox(handles.FeaturesListbox);
end

% --- #13 --- %
function TreeDepthPopup_CreateFcn(hObject, ~, ~) %#ok<DEFNU>
    % hObject    handle to TreeDepthPopup (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    empty - handles not created until after all CreateFcns called

    % Hint: popupmenu controls usually have a white background on Windows.
    %       See ISPC and COMPUTER.
    if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
        set(hObject,'BackgroundColor','white');
    end
end

% --- #14 --- %
function ThresholdPopup_CreateFcn(hObject, ~, ~) %#ok<DEFNU>
    % hObject    handle to ThresholdPopup (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    empty - handles not created until after all CreateFcns called

    % Hint: popupmenu controls usually have a white background on Windows.
    %       See ISPC and COMPUTER.
    if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
        set(hObject,'BackgroundColor','white');
    end
end

% --- #15 --- %
function CrossValidationCheckbox_Callback(hObject, ~, handles) %#ok<DEFNU>
    % hObject    handle to CrossValidationCheckbox (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    structure with handles and user data (see GUIDATA)

    if (get(hObject,'Value'))
        set(handles.TreeDepthPopup, 'Enable', 'off');
        set(handles.ThresholdPopup, 'Enable', 'off');
    else
        set(handles.TreeDepthPopup, 'Enable', 'on');
        set(handles.ThresholdPopup, 'Enable', 'on');
    end
end

% --- #16 --- %
function SAXWindowWidthPopup_CreateFcn(hObject, ~, ~) %#ok<DEFNU>
    % hObject    handle to SAXWindowWidthPopup (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    empty - handles not created until after all CreateFcns called

    % Hint: popupmenu controls usually have a white background on Windows.
    %       See ISPC and COMPUTER.
    if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
        set(hObject,'BackgroundColor','white');
    end
end

% --- #17 --- %
function SAXCardinalityPopup_CreateFcn(hObject, ~, ~) %#ok<DEFNU>
    % hObject    handle to SAXCardinalityPopup (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    empty - handles not created until after all CreateFcns called

    % Hint: popupmenu controls usually have a white background on Windows.
    %       See ISPC and COMPUTER.
    if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
        set(hObject,'BackgroundColor','white');
    end
end

% --- #18 --- %
function NumCandidatesClassPopup_CreateFcn(hObject, ~, ~) %#ok<DEFNU>
    % hObject    handle to NumCandidatesClassPopup (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    empty - handles not created until after all CreateFcns called

    % Hint: popupmenu controls usually have a white background on Windows.
    %       See ISPC and COMPUTER.
    if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
        set(hObject,'BackgroundColor','white');
    end
end

% --- #19 --- %
function MinShapeletTextbox_CreateFcn(hObject, ~, ~) %#ok<DEFNU>
    % hObject    handle to MinShapeletTextbox (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    empty - handles not created until after all CreateFcns called

    % Hint: edit controls usually have a white background on Windows.
    %       See ISPC and COMPUTER.
    if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
        set(hObject,'BackgroundColor','white');
    end
end

% --- #20 --- %
function MaxShapeletTextbox_CreateFcn(hObject, ~, ~) %#ok<DEFNU>
    % hObject    handle to MaxShapeletTextbox (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    empty - handles not created until after all CreateFcns called

    % Hint: edit controls usually have a white background on Windows.
    %       See ISPC and COMPUTER.
    if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
        set(hObject,'BackgroundColor','white');
    end
end
