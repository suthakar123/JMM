% ---  #4 --- %
function LoadDataButton_Callback(hObject, ~, handles) %#ok<DEFNU>
    % hObject    handle to LoadDataButton (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    structure with handles and user data (see GUIDATA)
    
    % Do nothing if we haven't loaded the config file yet
    if (isempty(getappdata(0, 'channelNames')))
        errordlg('Must first load config file!', 'Load Data Error');
        return;
    end
    
    % Reset the data structures
    [database, activities] = LoadDataFromDirectory({});
    handles.database = database;
    handles.activities = activities;
    
    % Once all of the data has been parsed, we want to populate the
    % subjects pop up
    % Preallocate based on the number of nonempty values
    popUpString = cell(1, sum(~cellfun(@isempty, handles.database)) + 1);
    popUpString{1} = 'Subject #';
    popUpIndex = 2;
    for index = 1:length(handles.database)
        if (~isempty(handles.database{index}))
            popUpString{popUpIndex} = num2str(index);
            popUpIndex = popUpIndex + 1;
        end
    end
    set(handles.SubjectPopUp, 'String', popUpString);
    set(handles.SubjectPopUp, 'Enable', 'on');
    set(handles.SubjectPopUp, 'Value', 1);
    
    % Make sure the trial and activity popups are reset
    set(handles.TrialPopUp, 'Enable', 'off');
    set(handles.TrialPopUp, 'Value', 1);
    set(handles.ActivityPopUp, 'Enable', 'off');
    set(handles.ActivityPopUp, 'Value', 1);
    
    % Update handles structure
    guidata(hObject, handles);
end
