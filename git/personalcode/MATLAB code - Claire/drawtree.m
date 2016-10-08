function [X,Y, root] = drawtree(root, fig, featureNames, classNames)
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
       display(1);
       
       set(h(1),'ButtonDownFcn',@labelpoint,'Tag','branch','MarkerSize',10);
       display(2);
       set(h(2),'ButtonDownFcn',@labelpoint,'Tag','leaf','MarkerSize',20);
       display(3);
       set(h(end),'HitTest','off','Tag','connection');
    else
       set(h,'ButtonDownFcn',@labelpoint,'Tag','leaf','MarkerSize',20);
       display(4);
    end

    % Label leaf nodes with class, branch nodes with split rule
    ctext = classNames(class(leafnodes));

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
    ctext = cell(length(branchnodes),1);
for j=1:length(branchnodes)
   k = branchnodes(j);
   if splitvar(k)>0
      ctext{j} = sprintf('   %s < %g',names{splitvar(k)},cutoff(k));
   else
      cats = root.catsplit{cutoff(k),1};
      if length(cats)==1
         ctext{j} = sprintf('   %s = %s',names{-splitvar(k)},num2str(cats));
      else
         ctext{j} = sprintf('   %s \\in (%s)',names{-splitvar(k)},num2str(cats));
      end
   end
end
    % Adjust layout of controls to fit figure
    layoutfig(fig);
end
