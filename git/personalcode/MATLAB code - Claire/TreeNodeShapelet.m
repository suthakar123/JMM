classdef TreeNodeShapelet < handle
    properties (SetAccess = private)
        SplitShapelet
        SplitChannel
        SplitThreshold
        ClassMembership
    end
    
    properties
        Parent
        LeftChild
        RightChild
    end
    
    methods
        function tN = TreeNodeShapelet(splitShapelet, splitChannel, splitThreshold, classMembership)
            tN.SplitShapelet = splitShapelet;
            tN.SplitChannel = splitChannel;
            tN.SplitThreshold = splitThreshold;
            tN.ClassMembership = classMembership;
        end
        
        function set.LeftChild(obj, leftChild)
            obj.LeftChild = leftChild;
        end
        
        function set.RightChild(obj, rightChild)
            obj.RightChild = rightChild;
        end
        
        function set.Parent(obj, parent)
            obj.Parent = parent;
        end
    end
end