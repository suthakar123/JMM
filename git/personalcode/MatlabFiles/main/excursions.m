%%  .............................................................................................|
%   
%   Excursion Analysis for PatientProcessing
%
%   04.30.16
%
%   Change Log:
%
% 
%
%................................................................................................|

function[err,max_flexion,max_extension,excursions_per_hour,median_excursion,trim_time,excursion_value]=excursions(CalibratedData,plotOption)
disp('excursions: Production version 04.30.16 ')
    err = 0;
    max_flexion = 0;
    max_extension = 0;
    excursions_per_hour = 0;
    median_excursion = 0;
    trim_time = 0;
    
    if (isempty(CalibratedData))
        err = 103;
        return;
    end
    
%%  1.  Set peakfinder, data integrity, and trim criteria........................................| 1

    %a. Peakfinder
    delta=10;  %   Must be >= delta above surrounding data to be counted
    thresh=5;  %   Must be >= thresh (max) or <thresh (min) to be counted
    aMax=140;  %   Largest acceptable angle (takes care of transients)
    
    num_pts=size(CalibratedData,1);  %Number of points in data stream

    %b. Data Integrity
    min_time=10;                 %Minimum time in minutes for dataset
    min_npts=min_time*20*60;     %Minimum number of values in dataset
    
    %C. Trim Criteria
    bin_size =15;                %Move backwards through dataset in bin_size intervals
    min_peaks=5;                 %Truncate intervals with < min_peaks peak values in 15 minutes            
    
%%  2. Find peaks and valleys....................................................................| 2

    %Peakfinder routine from MatlabCentral here:
    %http://www.mathworks.com/matlabcentral/fileexchange/25500
    %PEAKFINDER Noise tolerant fast peak finding algorithm
    %   INPUTS:
    %       x0 - A real vector from the maxima will be found (required)
    %       sel - The amount above surrounding data for a peak to be
    %           identified (default = (max(x0)-min(x0))/4). Larger values mean
    %           the algorithm is more selective in finding peaks.
    %       thresh - A thresholdpopup value which peaks must be larger than to be
    %           maxima or smaller than to be minima.
    %       extrema - 1 if maxima are desired, -1 if minima are desired
    %           (default = maxima, 1)
    %   OUTPUTS:
    %       peakLoc - The indicies of the identified peaks in x0
    %       peakMag - The magnitude of the identified peaks
    %
    %   [peakLoc] = peakfinder(x0) returns the indicies of local maxima that
    %       are at least 1/4 the range of the data above surrounding data.
    %
    %   [peakLoc] = peakfinder(x0,sel) returns the indicies of local maxima
    %       that are at least sel above surrounding data.
    %
    %   [peakLoc] = peakfinder(x0,sel,thresh) returns the indicies of local 
    %       maxima that are at least sel above surrounding data and larger
    %       (smaller) than thresh if you are finding maxima (minima).
    %
    %   [peakLoc] = peakfinder(x0,sel,thresh,extrema) returns the maxima of the
    %       data if extrema > 0 and the minima of the data if extrema < 0
    %
    %   [peakLoc, peakMag] = peakfinder(x0,...) returns the indicies of the
    %       local maxima as well as the magnitudes of those maxima
    %
    %   If called with no output the identified maxima will be plotted along
    %       with the input data.
    %
    %   Note: If repeated values are found the first is identified as the peak
    %
    
%%  3. Extract angle signal......................................................................| 3
        angle_data=CalibratedData(:,2);
    
    %
%%  4. Test incoming data integrity..............................................................| 4
    %
    %   Is data longer than min_npts?
        npts=size(angle_data,1);
        total_time=npts/20/60/60; %Record length in hrs
        
        if npts<min_npts;    %No  - return with error 101
            err=101;
            return
        end
                             %Yes - so continue

        
%%  5. Locate and save Peaks and Valleys.........................................................| 5
     
        %Locate peaks
        [peakLocIndex, peakMag]    =peakFinder(angle_data,delta,thresh,1);
        
        %locate valleys        
        %Set thresh to 120 since minima must be less than this value
        %all minima will be found
        [valleyLocIndex,valleyMag] =peakFinder(angle_data,delta,120,-1);
        
        if size(valleyLocIndex,2)==0;
            %no peaks found: Set return arguments to zero, erro code to 102 and return
            err=102;
            max_flexion   = 0;
            max_extension = 0;
            excursions_per_hour = 0;
            median_excursion = 0;
            trim_time=0;
            excursion_value=0;
            return
        end
     
        %Save peak and valley values for Flexion/Extension Display if requested later
        
        extension_peaks =valleyMag;
        flexion_peaks   =peakMag;
        
        %Save peakloc/valleyLoc as time in hours
        peakLocHrs  =peakLocIndex  /20/60/60;
        valleyLocHrs=valleyLocIndex/20/60/60;
        
        %Save total number of peaks and valleys
        npeaks   =numel(peakLocIndex);
        nvalleys =numel(valleyLocIndex);
        
%%  6.  Count excursions.........................................................................| 6
        %pre-allocate excursion arrays
        
        excursion_start    (1:20000)=nan;
        excursion_value    (1:20000)=nan;
   
        new_excursion_start    (1:20000)=nan;
        new_excursion_value    (1:20000)=nan;
        
        kount=0;
        
        %Loop from first peak to last peak -1
        for peak_num=1:npeaks-1;
            
                %Set a peak
                    peak_time =peakLocIndex(peak_num);
                    peak_value=peakMag(peak_num);

                if peak_num ~= 1;
                    %If not first peak (for which there will be no prior valley) continue
                    %Find excursion from last saved valley
                    kount=kount+1;
                    excursion_start    (kount)=valley_time;
                    excursion_value    (kount)=abs(peak_value-valley_value);
                end

                %Locate the next valley after this peak
                    %This loop allows for valleys that occured since last valley
                    %without a corresponding peak which can happen 
                    % with the peak detection algorithm

                    for j=1:nvalleys
                        if valleyLocIndex(j) > peak_time;
                            break
                        end
                    end
                    
                    %Break out index was first valley after current peak
                    valley_time =valleyLocIndex(j);
                    valley_value=valleyMag(j);

                %Increment excursion count and find excursion from last peak
                kount=kount+1;	
                excursion_start    (kount)=peak_time;
                excursion_value    (kount)=abs(peak_value-valley_value);
        end
                
%%	7. Apply thresholds to excursions ............................................................| 7
        
new_kount=0;
        for jv=1:kount
            if and(excursion_value(jv) >=delta,excursion_value(jv) <=aMax);
                new_kount=new_kount+1;
                new_excursion_start(new_kount)     =excursion_start(jv);
                new_excursion_value(new_kount)     =excursion_value(jv);
            end
        end
        
        %Replace old with new
        clear excursion_start; 
        clear excursion_duration;
        clear excursion_value;
        excursion_value     =new_excursion_value(1:new_kount);
        num_excursions      =new_kount;

       
%%	8.  Trim the end of the dataset..............................................................| 8
        
        %Move backwards through the datset
        % Determine the number of peaks in each bin_size minutes of data
        % Peak indices are in peakLoc
        
        % Preallocate val
        
        val(10000)=nan;
        
        step=bin_size*20*60;
        bin_num=0;
        bin_start_index = zeros(1, ceil((npts - step)/step));
        for jk=npts:-step:step; 
            bin_num=bin_num+1;
            edges(1)=jk-step+1;
            edges(2)=jk;
            n=histc(peakLocIndex,edges);
            val(bin_num) =n(1)+n(2); 
            %disp(['bin#', num2str(bin_num),':-',num2str(edges(1)),'-',num2str(edges(2)),'val=',num2str(val(bin_num))]);
            %Save starting index of this bin
            bin_start_index(bin_num) =edges(1);
        end

%   Moving backwards from end of data, locate first interval with > min_peaks

        for j=1:size(val,2)
            if val(j)>min_peaks; 
            trunc=j-1; %End at last bin not meting critierion
                if trunc==0
                    %No truncation required
                    trim_time=0;
                    break
                end
                
                trim_time=bin_start_index(trunc)/20/60/60;
                %disp(['Truncated at bin#',num2str(trunc)]);
                %disp(['Trimmed at bin#',num2str(trunc),'_',num2str(trim_time),' hrs'])
                break
            end
        end

        
%%  9.  Calculate return values
%
%       The average of the 50 largest flexion and extension peaks in the knee angle data

        ordered_flexion_peaks   = sort(flexion_peaks);
        ordered_extension_peaks = sort(extension_peaks); 
        %disp('Starting the Display');
        %disp(npeaks);
        %disp(ordered_extension_peaks);

        
        if npeaks>20
            max_flexion   = mean(ordered_flexion_peaks(npeaks-19:npeaks));    %The last  20 values (flexion increases the angle)
        else
            max_flexion   = mean(ordered_flexion_peaks(1:npeaks));
        end
        if nvalleys>20
            max_extension = mean(ordered_extension_peaks(1:20));              %The first 20 values (0 degrees is full extension)
        else 
            max_extension = mean(ordered_extension_peaks(1:nvalleys));
        end
         
%       Number of excursions per hour
        if trim_time==0;
            excursions_per_hour=num_excursions/total_time;
        else
            excursions_per_hour=num_excursions/trim_time;
        end


%       Median Excursion
        median_excursion =median(excursion_value,2);
        
        
%%  10. Optional Plot............................................................................| 10
        if (plotOption==1)

            figure(3)

            %%  Plot angle vs. time in figure 3
            time=(1:size(angle_data,1))/20/60/60;
            plot(time,angle_data,'-k')
            hold on

            xlabel('Time (hrs)   .','fontsize',15)
            ylabel('Angle (deg)  .','fontsize',15)
            title('Maxes and Mins of Excursions in Knee Angle','fontsize',35)
            
            %%  Overplot peaks and valleys
            max_time=time(num_pts);
            plot(peakLocHrs,peakMag,'ro')
            plot(valleyLocHrs,valleyMag,'bx')
            axis([0 max_time -90 180])
            if trim_time==0;
               %Draw in angle=0 in black 
               plot([0 max_time], [0 0],'k');
            else  
               %Draw in angle=0 in black
               plot([0 max_time], [0 0],'k');
               %Draw in trim time in red
               plot([trim_time trim_time], [0 120],'g-','LineWidth',2)
            end
            
            figure (4)
            a=size(excursion_value,2);
            n=histogram(excursion_value);
            xlabel('Excursion Angle (deg)')
            ylabel('Number of Excursions')
            
    
        end
end
