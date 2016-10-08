% TABLE OF CONTENTS:
%
%  #1 - ConfigGUI:
%           Necessary initialiazation code for the GUI
%  #2 - ConfigGUI_OpeningFcn:
%           Executes just before ConfigGUI is made visible
%  #3 - ConfigGUI_OutputFcn:
%           Outputs from this function are returned to the command line
%  #4 - ChannelNameText_CreateFcn:
%           Adjusts the ChannelNameText colors for Windows machines
%  #5 - SamplingRateText_CreateFcn:
%           Adjusts the SamplingRateText colors for Windows machines
%  #6 - SaveConfigButton_Callback:
%           Validates the user input and saves the config data to a file

% ---  #1 --- %
function varargout = ConfigGUI(varargin)
    % CONFIGGUI MATLAB code for ConfigGUI.fig
    %      CONFIGGUI, by itself, creates a new CONFIGGUI or raises the existing
    %      singleton*.
    %
    %      H = CONFIGGUI returns the handle to a new CONFIGGUI or the handle to
    %      the existing singleton*.
    %
    %      CONFIGGUI('CALLBACK',hObject,eventData,handles,...) calls the local
    %      function named CALLBACK in CONFIGGUI.M with the given input arguments.
    %
    %      CONFIGGUI('Property','Value',...) creates a new CONFIGGUI or raises the
    %      existing singleton*.  Starting from the left, property value pairs are
    %      applied to the GUI before ConfigGUI_OpeningFcn gets called.  An
    %      unrecognized property name or invalid value makes property application
    %      stop.  All inputs are passed to ConfigGUI_OpeningFcn via varargin.
    %
    %      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
    %      instance to run (singleton)".
    %
    % See also: GUIDE, GUIDATA, GUIHANDLES

    % Edit the above text to modify the response to help ConfigGUI

    % Last Modified by GUIDE v2.5 30-Nov-2012 16:50:00

    % Begin initialization code - DO NOT EDIT
    gui_Singleton = 1;
    gui_State = struct('gui_Name',       mfilename, ...
                       'gui_Singleton',  gui_Singleton, ...
                       'gui_OpeningFcn', @ConfigGUI_OpeningFcn, ...
                       'gui_OutputFcn',  @ConfigGUI_OutputFcn, ...
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
function ConfigGUI_OpeningFcn(hObject, ~, handles, varargin)
    % This function has no output args, see OutputFcn.
    % hObject    handle to figure
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    structure with handles and user data (see GUIDATA)
    % varargin   command line arguments to ConfigGUI (see VARARGIN)

    % Choose default command line output for ConfigGUI
    handles.output = hObject;

    % Populate the fields based on the current values in the root
    set(handles.ChannelNameText, 'String', getappdata(0, 'channelNames'));
    set(handles.SamplingRateText, 'String', getappdata(0, 'samplingRate'));
    
    % Update handles structure
    guidata(hObject, handles);

    % UIWAIT makes ConfigGUI wait for user response (see UIRESUME)
    % uiwait(handles.configFigure);
end

% ---  #3 --- %
function varargout = ConfigGUI_OutputFcn(~, ~, handles) 
    % varargout  cell array for returning output args (see VARARGOUT);
    % hObject    handle to figure
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    structure with handles and user data (see GUIDATA)

    % Get default command line output from handles structure
    varargout{1} = handles.output;
end

% ---  #4 --- %
function ChannelNameText_CreateFcn(hObject, ~, ~) %#ok<DEFNU>
    % hObject    handle to ChannelNameText (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    empty - handles not created until after all CreateFcns called

    % Hint: edit controls usually have a white background on Windows.
    %       See ISPC and COMPUTER.
    if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
        set(hObject,'BackgroundColor','white');
    end
end

% ---  #5 --- %
function SamplingRateText_CreateFcn(hObject, ~, ~) %#ok<DEFNU>
    % hObject    handle to SamplingRateText (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    empty - handles not created until after all CreateFcns called

    % Hint: edit controls usually have a white background on Windows.
    %       See ISPC and COMPUTER.
    if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
        set(hObject,'BackgroundColor','white');
    end
end

% ---  #6 --- %
function SaveConfigButton_Callback(~, ~, handles) %#ok<DEFNU>
    % hObject    handle to SaveConfigButton (see GCBO)
    % eventdata  reserved - to be defined in a future version of MATLAB
    % handles    structure with handles and user data (see GUIDATA)
    
    samplingRate = str2double(get(handles.SamplingRateText, 'String'));
    channelNames = cellstr(get(handles.ChannelNameText, 'String'));
    
    % Need to make sure that the value specified for the sampling rate is a
    % number
    if (isnan(samplingRate))
        errordlg('Sampling rate specified is not a number', 'Save Config Error');
    else
        % Also need to make sure that the channel names are not empty
        if (isempty(channelNames))
            errordlg('No channel names specified', 'Save Config Error');
        else
            % Save the config data to a user specified file and close the
            % ConfigGUI
            [filename, pathname] = uiputfile('*.mat', 'Save Config File As');
            if (filename == 0)
                errordlg('Improper file specified', 'Save Config Error');
            else
                % Only save if the user specified a proper file
                save([pathname, filename], 'channelNames', 'samplingRate');
                
                % Save the data in the root so that it can be used in the
                % LearningGUI
                setappdata(0, 'samplingRate', samplingRate);
                setappdata(0, 'channelNames', channelNames);
                
                % Close the GUI once everything is finished
                close(handles.configFigure);
            end
        end
    end
end
