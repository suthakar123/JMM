function ViewTree(root, featureNames, classNames)
    % Create empty figure and axes to receive tree display
    fig = setupfigure;
    
    try
       adjustmenu(fig);
    catch %#ok<CTCH>
    end
    
    % Draw tree
    %[X,Y] = drawtree(root,fig,featureNames,classNames);
    [X,Y] = drawtree(root,fig,featureNames,classNames);
    % Save information for call-backs
    set(fig,'ButtonDownFcn',@removelabels, ...
            'UserData',{X Y 0 featureNames classNames root});
        
    
end

% ----------------------------------------------
function [X,Y] = drawtree(root, fig, featureNames, classNames)
    ax = get(fig,'CurrentAxes');

    [parentnode,children,splitvar,cutoff,class] = ...
        getTreePropertiesForDraw(root,[],[],{},[],[],0);
    nonroot = parentnode~=0;
    
    % Get coordinates of nodes
    isleaf = boolean(zeros(length(splitvar), 1));
    for i = 1:length(splitvar)
        if (length(splitvar{i}) == 1 && splitvar{i} == 0)
            isleaf(i) = 1;
        end
    end
    [X,Y] = layouttree(isleaf,parentnode,children);
 
    isbranch = ~isleaf;
    if any(isbranch)
       isleaf = false(size(isbranch));
       c = children(isbranch,:);
       c = c(c>0);
       isleaf(c) = 1;
       isleaf = isleaf & ~isbranch;
    else
       isleaf = ~nonroot;
    end

    branchnodes = find(isbranch);
    leafnodes = find(isleaf);
    
    % Get coordinates of connecting lines
    p = parentnode(nonroot);
    x = [X(nonroot)'; X(p)'; NaN+p'];
    y = [Y(nonroot)'; Y(p)'; NaN+p'];

    % Plot nodes and connections for nodes, but stop listening to axis
    % and remember some things that may get changed during the plotting
    axislistener(ax,false);
    xlim = get(ax,'XLim');
    ylim = get(ax,'YLim');
    ud = get(ax,'UserData');
    h = plot(X(branchnodes),Y(branchnodes),'b^', ...
             X(leafnodes),Y(leafnodes),'b.', ...
             x(:),y(:),'b-','Parent',ax);
    set(ax,'UserData',ud,'Visible','off','XLim',xlim,'YLim',ylim);
    axislistener(ax,true);

    if length(h)==3
       
       set(h(1),'ButtonDownFcn',@labelpoint,'Tag','branch','MarkerSize',10);
       %set(gca,'ButtonDownFcn',@labelpoint);
       
       set(h(2),'ButtonDownFcn',@labelpoint,'Tag','leaf','MarkerSize',20);
       
       set(h(end),'HitTest','off','Tag','connection');
       
    else
       set(h,'ButtonDownFcn',@labelpoint,'Tag','leaf','MarkerSize',20);
       
    end

    % Label leaf nodes with class, branch nodes with split rule
    ctext = classNames(class(leafnodes));
    disp(6);
    
    h = findobj(fig,'Tag','menuleaf');
    vis = get(h,'Checked');
    text(X(leafnodes),Y(leafnodes),ctext,'HitTest','off','Parent',ax,...
             'VerticalAlignment','top','HorizontalAlignment','center',...
              'Tag','leaflabel','Clipping','on','Visible',vis,'Interpreter','none');

    lefttext = cell(length(branchnodes),1);
    righttext = cell(length(branchnodes),1);
    for j=1:length(branchnodes)
       k = branchnodes(j);
       cut = cutoff(k);
       if length(splitvar{k}) == 1 && splitvar{k}>0
          varname = featureNames{splitvar{k}};
       else
          varname = 'Distance';
       end
       lefttext{j} = sprintf('%s < %g   ',varname,cut);
       righttext{j} = sprintf('  %s >= %g',varname,cut);
    end

    h = findobj(fig,'Tag','menubr');
    vis = get(h,'Checked');
    text(X(branchnodes),Y(branchnodes),lefttext,'HitTest','off','Parent',ax,...
         'Tag','branchlabel','Clipping','on','Visible',vis,'Interpreter','none',...
         'HorizontalAlignment','right');
    text(X(branchnodes),Y(branchnodes),righttext,'HitTest','off','Parent',ax,...
         'Tag','branchlabel','Clipping','on','Visible',vis,'Interpreter','none',...
         'HorizontalAlignment','left');

    % Show pruned nodes or not as desired
    doprunegraph(fig);
     
    % Adjust axes contents
    dozoom(fig);

    % Adjust layout of controls to fit figure
    layoutfig(fig);
end


   

% ----------------------------------------------
function [X,Y] = layouttree(isleaf,parents,children)
%LAYOUTTREE Select x,y coordinates of tree elements.
    n = length(isleaf);
    X = zeros(n,1);
    Y = X;
    layoutstyle = 1;

    % Plot top node on one level, its children at next level, etc.
    for j=1:n
       p = parents(j);
       if p>0
          Y(j) = Y(p)+1;
       end
    end
    if layoutstyle==1
       % Layout style 1
       % Place terminal nodes, then put parents above them

       % First get preliminary placement, used to position leaf nodes
       for j=1:n
          p = parents(j);
          if p==0
             X(j) = 0.5;
          else
             dx = 2^-(Y(j)+1);
             if j==children(p,1)
                X(j) = X(p) - dx;
             else
                X(j) = X(p) + dx;
             end
          end
       end

       % Now make leaf nodes equally spaced, preserving their order
       leaves = find(isleaf);
       nleaves = length(leaves);
       [~,b] = sort(X(leaves));
       X(leaves(b)) = (1:nleaves) / (nleaves+1);

       % Position parent nodes above and between their children
       for j=max(Y):-1:0
          a = find(~isleaf & Y==j);
          c = children(a,:);
          X(a) = (X(c(:,1))+X(c(:,2)))/2;
       end
    else
       % Layout style 2
       % Spread out the branch nodes, somewhat under their parent nodes
       X(Y==0) = 0.5;
       for j=1:max(Y)
          vis = (Y==j);                  % real nodes at this level
          invis = (Y==(j-1) & isleaf);   % invisible nodes to improve layout
          nvis = sum(vis);
          nboth = nvis + sum(invis);
          x = [X(parents(vis))+1e-10*(1:nvis)'; X(invis)];
          [xx,xidx] = sort(x);
          xx(xidx) = 1:nboth;
          X(vis) = (xx(1:nvis) / (nboth+1));
       end
    end

    k = max(Y);
    Y = 1 - (Y+0.5)/(k+1);
end

% ----------------------------------------------
function [parents,children,splitvar,cutoff,class] = getTreePropertiesForDraw(node,...
    parents,children,splitvar,cutoff,class,parentNum)
%GETTREEPROPERTIESFORDRAW Extracts all of the main properties from the tree

    parents = [parents; parentNum];
    children = [children; 0 0];

    if (isa(node, 'TreeNode') || isa(node, 'TreeNodeShapelet'))
        if (isa(node, 'TreeNode'))
            splitvar{length(splitvar) + 1} = node.SplitFeature;
        else
            splitvar{length(splitvar) + 1} = node.SplitShapelet;
        end
        cutoff = [cutoff; node.SplitThreshold];
        class = [class; 0];
        
        nodeNum = length(splitvar);
        leftChildNum = nodeNum + 1;
        [parents, children, splitvar, cutoff, class] = getTreePropertiesForDraw(node.LeftChild,...
            parents, children, splitvar, cutoff, class, nodeNum);
        rightChildNum = length(splitvar) + 1;
        [parents, children, splitvar, cutoff, class] = getTreePropertiesForDraw(node.RightChild,...
            parents, children, splitvar, cutoff, class, nodeNum);
        children(nodeNum, 1) = leftChildNum;
        children(nodeNum, 2) = rightChildNum;
    else
        splitvar{length(splitvar) + 1} = 0;
        cutoff = [cutoff; 0];
        class = [class; node.PredictedClass];
    end
end

% ----------------------------------------------
function fig = setupfigure
%SETUPFIGURE Set up uicontrols on decision tree figure.
    fig = figure('IntegerHandle','off', 'NumberTitle','off', ...
                 'Units','points','PaperPositionMode','auto',...
                 'Tag','tree viewer');
    ax = axes('Parent',fig,'UserData',cell(1,4),'XLim',0:1,'YLim',0:1);

    % Set default print options
    pt = printtemplate;
    pt.PrintUI = 0;
    set(fig,'PrintTemplate',pt)

    % Arrange figure contents
    pos = [0 0 1 1];
    set(ax,'Visible','off','XLim',0:1,'YLim',0:1,'Position',pos);
    set(ax,'Units','points');
    apos = get(ax,'Position');
    fpos = get(fig,'Position');
    hframe = uicontrol(fig,'Units','points','Style','frame',...
                       'Position',[0 0 1 1],'Tag','frame');
                   
    figtitle = 'Classification Tree Viewer';

    % Tip-related items
    h=uicontrol(fig,'units','points','Tag','clicktext',...
                'String','Click to display:',...
                'style','text','HorizontalAlignment','left','FontWeight','bold');
    extent = get(h,'Extent');
    theight = extent(4);
    aheight = apos(4);
    tbottom = aheight - 1.5*theight;
    posn = [2, tbottom, 150, theight];
    set(h,'Position',posn);
    textpos = posn;
    e = get(h,'Extent');
    choices = sprintf('%s|%s|%s|%s',...
       'Identity',...
       'Feature Ranges',...
       'Class Membership',...
       'Estimated Probabilities');
    
    %choices = 'Identity|Variable ranges|Class membership|Estimated probabilities';
    
    posn = [e(1)+e(3)+2, aheight-1.25*theight, 155, theight];
    uicontrol(fig,'units','points','position',posn,'Tag','clicklist',...
                'String',choices, 'Style','pop','BackgroundColor',ones(1,3),...
                'Callback',@removelabels);
    set(ax,'Position',[0 0 apos(3) tbottom]);
    set(fig,'Toolbar','figure', 'Name',figtitle,'HandleVisibility','callback');

    % Magnification items
    textpos(1) = posn(1) + posn(3) + 10;
    h=uicontrol(fig,'units','points','Tag','magtext','Position',textpos,...
                'String','Magnification:',...
                'style','text','HorizontalAlignment','left','FontWeight','bold');
    e = get(h,'Extent');
    textpos(3) = e(3);
    set(h,'Position',textpos);
    posn = [textpos(1)+textpos(3)+2, posn(2), 80, posn(4)];
    h=uicontrol(fig,'units','points','position',posn,'Tag','maglist',...
                'String','x', 'Style','pop','BackgroundColor',ones(1,3),...
                'Callback',@domagnif);
    adjustcustomzoom(h,false);

    % Adjust frame position
    lowest = min(posn(2),textpos(2))-2;
    frpos = apos;
    frpos(4) = 1.1*(apos(4)-lowest);
    frpos(2) = apos(4) - frpos(4);
    set(hframe,'Position',frpos);

    % Add scroll bars, start out invisible
    h1 = uicontrol(fig,'Style','slider','Tag','hslider','Visible','off',...
                   'Units','points','Callback',@dopan);
    p1 = get(h1,'Position');
    sw = p1(4);               % default slider width
    p1(1:2) = 1;
    p1(3) = fpos(3)-sw;
    set(h1,'Position',p1);
    p2 = [fpos(3)-sw, sw, sw, frpos(2)-sw];
    uicontrol(fig,'Style','slider','Tag','vslider','Visible','off',...
                   'Units','points','Position',p2,'Callback',@dopan);

    % Add new menu before the Window menu
    hw = findall(fig,'Type','uimenu','Tag','figMenuWindow');
    h0 = uimenu(fig,'Label','Tree',...
        'Position',get(hw,'Position'));
    uimenu(h0, 'Label','Label Branch Nodes', ...
        'Position',1,'Tag','menubr','Checked','on','Callback',@domenu);
    uimenu(h0, 'Label','Label Leaf Nodes', ...
        'Position',2,'Tag','menuleaf','Checked','on','Callback',@domenu);

    set(fig,'ResizeFcn',@resize);
end

% --------------------------------------------------
function adjustcustomzoom(o,add)
%ADJUSTCUSTOMZOOM Add or remove special custom magnification level
    nchoices = size(get(o,'String'),1);
    choices = '100%|200%|400%|800%';
    if ~add && nchoices~=4
       set(o,'String',choices);
    elseif add && nchoices~=5
       choices = [choices '|' 'Custom'];
       set(o,'String',choices);
    end
end

% ----------------------------------------------
function resize(varargin)
%RESIZE Resize figure showing decision tree.
    layoutfig(gcbf)
end

% ----------------------------------------------
function layoutfig(f)
%LAYOUTFIG Layout figure contents

    set(f,'Units','points');
    fpos = get(f,'Position');

    % Resize frame
    h = findobj(f,'Tag','frame');
    frpos = get(h,'Position');
    frpos(2) = fpos(4) - frpos(4);
    frpos(3) = fpos(3);
    set(h,'Position',frpos);

    % Resize controls inside frame
    tags = {'clicktext'  'clicklist'  'magtext' 'maglist' };
    mult = [1.6          1.35         1.6       1.35  ];
    for j=1:length(tags)
       h = findobj(f,'Tag',tags{j});
       p = get(h,'Position');
       if j==1, theight = p(4); end
       p(2) = fpos(4) - mult(j)*theight;
       set(h,'Position',p);
    end

    % Resize sliders
    hh = findobj(f,'Tag','hslider');
    hv = findobj(f,'Tag','vslider');
    p1 = get(hh,'Position');
    sw = p1(4);
    p1(3) = frpos(3) - sw - 1;
    set(hh,'Position',p1);
    p2 = get(hv,'Position');
    p2(1) = frpos(3) - sw - 1;
    p2(4) = frpos(2) - sw - 1;
    set(hv,'Position',p2);
    if isequal(get(hh,'Visible'),'off')
       sw = 0;
    end

    % Resize axes
    h = get(f,'CurrentAxes');
    p = [0, sw, frpos(3)-sw, frpos(2)-sw];
    set(h,'Position',p);
end

% ------------------------------------------
function domagnif(varargin)
%DOMAGNIF React to magnification level change

    f = gcbf;
    o = gcbo;

    % We need sliders if the magnification level is > 100%
    h = [findobj(f,'Tag','hslider'), findobj(f,'Tag','vslider')];
    maglevel = get(o,'Value');
    if maglevel==1
       set(h,'Visible','off');
    else
       set(h,'Visible','on');
    end

    % Adjust layout if necessary
    resize;

    % Adjust axes contents
    dozoom(f);

    % Remove custom zoom amount from list if not in use
    if maglevel<=4
       adjustcustomzoom(o,false);
    end

    % Turn off any manual zooming
    zoom(f,'off');
end

% ----------------------------------------------
function removelabels(varargin)
%REMOVELABELS Remove any labels remaining on the graph.

    f = gcbf;
    delete(findall(f,'Tag','LinescanMarker'));
    delete(findall(f,'Tag','LinescanText'));
end

% ------------------------------------------
function dozoom(f)
%DOZOOM Adjust axes contents to match magnification settings

    a = get(f,'CurrentAxes');
    hh = findobj(f,'Tag','hslider');
    hv = findobj(f,'Tag','vslider');
    hm = findobj(f,'Tag','maglist');

    % Get information about x/y ranges and current midpoint
    bigxlim = get(hh,'UserData');
    bigylim = get(hv,'UserData');
    xlim = get(a,'XLim');
    ylim = get(a,'YLim');
    currx = (xlim(1)+xlim(2))/2;
    curry = (ylim(1)+ylim(2))/2;

    % How much are we magnifying each axis?
    magfact = [1 2 4 8];
    mag = get(hm,'Value');
    if mag<=4
       magfact = magfact(mag)*ones(1,2);
    else
       magfact = [diff(bigxlim)/diff(xlim), diff(bigylim)/diff(ylim)];
    end
    magfact = max(magfact,1);

    if all(magfact==1)                 % no magnification
       xlim = bigxlim;
       ylim = bigylim;
    else                               % magnify by showing a subset of range
       magfact = max(magfact,1.01);
       dx = diff(bigxlim)/magfact(1);
       dy = diff(bigylim)/magfact(2);
       xval = max(bigxlim(1), min(bigxlim(2)-dx, currx-dx/2));
       xlim = xval + [0,dx];
       yval = max(bigylim(1), min(bigylim(2)-dy, curry-dy/2));
       ylim = yval + [0,dy];
       set(hh,'Min',bigxlim(1),'Max',bigxlim(2)-dx,'Value',xval);
       set(hv,'Min',bigylim(1),'Max',bigylim(2)-dy,'Value',yval);
    end
    axislistener(a,false);
    set(a,'XLim',xlim,'YLim',ylim);
    axislistener(a,true);
end

% -----------------------------------------
function axislistener(a,enable)
%AXISLISTENER Enable or disable listening to axis limit changes

    f = get(a,'Parent');
    ud = get(a,'UserData');
    if enable
       % Start listening to axis limit changes
       list1 = addlistener(a, 'XLim', 'PostSet', @(src,evt) customzoom(f));
       list2 = addlistener(a, 'YLim', 'PostSet', @(src,evt) customzoom(f));
       ud(3:4) = {list1 list2};
    else
       % Delete listeners
       for j=3:4
          lstnr = ud{j};
          if ~isempty(lstnr), delete(lstnr); end
       end
       ud(3:4) = {[]};
    end
    set(a,'UserData',ud);
end

% -----------------------------------------
function customzoom(f)
%CUSTOMPAN Deal with panning of a zoomed tree view

    a = get(f,'CurrentAxes');
    xlim = get(a,'XLim');
    ylim = get(a,'YLim');

    hh = findobj(f,'Tag','hslider');
    hv = findobj(f,'Tag','vslider');
    hm = findobj(f,'Tag','maglist');

    bigxlim = get(hh,'UserData');
    bigylim = get(hv,'UserData');
    magfact = [1 2 4 8];

    % Figure out if we have a standard zoom amount (100%, 200%, etc.) or
    % a custom zoom amount
    xratio = diff(bigxlim) / diff(xlim);
    yratio = diff(bigylim) / diff(ylim);
    standard = abs(xratio-yratio)<=0.02 & abs(xratio-round(xratio))<=0.02 ...
                                        & abs(yratio-round(yratio))<=0.02;
    if standard
       xratio = round(xratio);
       standard = ismember(xratio,magfact);
    end

    % Update the magnification setting
    if standard
       set(hm,'Value',find(magfact==xratio));
       adjustcustomzoom(hm,false);
       if xratio==1
          h = [findobj(f,'Tag','hslider'), findobj(f,'Tag','vslider')];
          set(h,'Visible','off');
       end
    else
       adjustcustomzoom(hm,true);
       set(hm,'Value',5);
       h = [findobj(f,'Tag','hslider'), findobj(f,'Tag','vslider')];
       set(h,'Visible','on');
    end

    dozoom(f);
end

% ------------------------------------------
function doprunegraph(f)
%DOPRUNEGRAPH Adjust graph to show full/pruned setting

    a = get(f,'CurrentAxes');
    
    xlim = get(a,'XLim');
    ylim = get(a,'YLim');
    bigxlim = 0:1;
    bigylim = 0:1;

    axislistener(a,false);
    set(a,'XLim',xlim,'YLim',ylim);
    axislistener(a,true);
    hh = findobj(f,'Tag','hslider');
    set(hh,'UserData',bigxlim);
    hv = findobj(f,'Tag','vslider');
    set(hv,'UserData',bigylim);
end

% ------------------------------------------
function domenu(varargin)
%DOMENU Carry out menu actions for tree viewer.

    o = gcbo;
    f = gcbf;
    t = get(o,'Tag');
    switch(t)
     % Turn on/off branch labels
     case 'menubr'
       curval = get(o,'Checked');
       if isequal(curval,'on')
          set(o,'Checked','off');
          h = findobj(f,'Type','text','Tag','branchlabel');
          set(h,'Visible','off');
       else
          set(o,'Checked','on');
          h = findobj(f,'Type','text','Tag','branchlabel');
          set(h,'Visible','on');
       end

     % Turn on/off leaf labels
     case 'menuleaf'
       curval = get(o,'Checked');
       if isequal(curval,'on')
          set(o,'Checked','off');
          h = findobj(f,'Type','text','Tag','leaflabel');
          set(h,'Visible','off');
       else
          set(o,'Checked','on');
          h = findobj(f,'Type','text','Tag','leaflabel');
          set(h,'Visible','on');
       end
    end
end

% ----------------------------------------------
function [foundNode, foundNum] = findNodeForLabelPoint(node, curNum, numToFind)
%FINDNODEFORLABELPOINT Find the node object associated with the given number
    foundNum = curNum + 1;
    foundNode = [];

    if (foundNum == numToFind)
        foundNode = node;
    else
        if (isa(node, 'TreeNode') || isa(node, 'TreeNodeShapelet'))
            [foundNode, foundNum] = findNodeForLabelPoint(node.LeftChild, foundNum, numToFind);
            if (foundNum ~= numToFind)
                [foundNode, foundNum] = findNodeForLabelPoint(node.RightChild, foundNum, numToFind);
            end
        end
    end
end


% ----------------------------------------------
function labelpoint(varargin)
%LABELPOINT Label point on tree in response to mouse click.
    
    h = gcbo;
    f = gcbf;
    stype = get(f,'SelectionType');
    if ~isequal(stype,'alt') && ~isequal(stype,'extend')
       removelabels;
    end
    t = get(h,'Tag');
    if isequal(t,'branch') || isequal(t,'leaf')
       ud = get(f,'UserData');
       X = ud{1};       % x coordinates
       Y = ud{2};       % y coordinates
       varnames = ud{4};% variable names
       cnames = ud{5};  % class names
       root = ud{6};    % complete tree

       % Find closest point
       ax = get(f,'CurrentAxes');
       cp = get(ax,'CurrentPoint');
       [~,node] = min(abs(X-cp(1,1)) + abs(Y-cp(1,2)));
       
       treeNode = findNodeForLabelPoint(root, 0, node);

       uih = findobj(f,'Tag','clicklist');
       labeltype = get(uih,'Value');
       shapelet = [];
       
       if isequal(labeltype,4)
          % Show fitted class probabilities
          N = sum(treeNode.ClassMembership);
          txt = 'Class probabilities';
          display('2');
          for j=1:length(treeNode.ClassMembership);
             txt = sprintf('%s\n%s = %.3g',txt,cnames{j},treeNode.ClassMembership(j)/N);
          end
          display('3');
       elseif isequal(labeltype,3)
          % Show class membership in data
          N = sum(treeNode.ClassMembership);
          txt = sprintf('%s = %d','Total data points',N);
          for j=1:length(treeNode.ClassMembership);
             txt = sprintf('%s\n%d %s',txt,treeNode.ClassMembership(j),cnames{j});
          end

       elseif isequal(labeltype,1)
          % Get a display of the split rule at branch nodes,
          % or the majority class at leaf nodes
          if (isa(treeNode, 'TreeLeafShapelet'))
             txt = sprintf('%s %d (%s)\n%s: %s',...
                'Node',...
                node,...
                'leaf',...
                'Class',...
                cnames{treeNode.PredictedClass});
          elseif (isa(treeNode, 'TreeNode'))
             txt = sprintf('%s %d (%s)\n%s:  %s < %g',...
                'Node',...
                node,...
                'branch',...
                'Rule',...
                varnames{treeNode.SplitFeature},...
                treeNode.SplitThreshold);
          else
              txt = sprintf('%s %d (%s)', 'Node', node, 'branch');
              shapelet = treeNode.SplitShapelet;
          end
          
       elseif isequal(labeltype,2)
          if (isa(treeNode, 'TreeLeafShapelet'))
              txt = 'Leaf Node';
          elseif (isa(treeNode, 'TreeNode'))
              % Show feature ranges in data
              txt = 'Feature ranges (3 sd):';
              display('4');
              for j=1:length(treeNode.ClassMembership);
                 if (treeNode.ClassMembership(j) > 0)
                     txt = sprintf('%s\n%s: [%.3g, %.3g]',txt,cnames{j},...
                         treeNode.ClassMeans(j) - 3 * treeNode.ClassStds(j),...
                         treeNode.ClassMeans(j) + 3 * treeNode.ClassStds(j));
                 else
                     txt = sprintf('%s\n%s: No data points',txt,cnames{j});
                 end
              end
          else
              txt = 'N/A';
          end
       else
          txt = '';
       end

       % Add label
       if ~isempty(txt)
          x = X(node);
          y = Y(node);
          xlim = get(gca,'xlim');
          ylim = get(gca,'ylim');
          if x<mean(xlim)
             halign = 'left';
             dx = 0.02;
          else
             halign = 'right';
             dx = -0.02;
          end
          if y<mean(ylim)
             valign = 'bottom';
             dy = 0.02;
          else
             valign = 'top';
             dy = -0.02;
          end
          h = text(x+dx*diff(xlim),y+dy*diff(ylim),txt,'Interpreter','none'); 
          yellow = [1 1 .85];
          set(h,'backgroundcolor',yellow,'margin',3,'edgecolor','k',...
                'HorizontalAlignment',halign,'VerticalAlignment',valign,...
                'tag','LinescanText','ButtonDownFcn',@startmovetips);
          line(x,y,'Color',yellow,'Marker','.','MarkerSize',20,...
               'Tag','LinescanMarker');
       end
       
       % Plot shapelet if that was requested
       if (~isempty(shapelet))
           figure('name', ['Split shapelet for node ', num2str(node)]);
           plot(shapelet);
           channelNames = getappdata(0, 'channelNames');
           title(channelNames{treeNode.SplitChannel});
       end
    end
end

function dopan(varargin)
%DOPAN Pan around magnified tree display

f = gcbf;
a = get(f,'CurrentAxes');
o = gcbo;
val = get(o,'Value');

axislistener(a,false);
if isequal(get(o,'Tag'),'hslider')
   bigxlim = get(o,'UserData');
   xlim = get(a,'XLim');
   xlim = xlim + (val-xlim(1));
   set(a,'XLim',xlim);
else
   bigylim = get(o,'UserData');
   ylim = get(a,'YLim');
   ylim = ylim + (val-ylim(1));
   set(a,'YLim',ylim);
end
axislistener(a,true);
end

