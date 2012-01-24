function initDragNDrop() {

    var droppedItems = {};
    initWidgets();
    registerEventHandlers();


    function initWidgets() {

        // make participants draggable
        $(".participant").each(function () {
            var containmentObjectId = calculateContainmentObjectId($(this));
            $(this).draggable({containment:containmentObjectId});
        });

        // convert associationcells into drop targets
        $(".associationCell").droppable();

        // hide association table cells' input fields
        $('.associationCell input').hide();

        positionParticipants();
    }

    function registerEventHandlers() {
        $(".associationCell").bind("dropover", function (event, ui) {
            onDropOver($(this), ui.draggable);
        });

        $(".associationCell").bind("dropout", function (event, ui) {
            onDropOut($(this), ui.draggable);
        });

        $(".participant").bind("dragstop", function () {
            onDragStop($(this));
        })
    }

    function setFieldValue(fieldId, value) {
        $("#" + fieldId).attr("value", value);
    }

    function resetFieldValue(fieldId) {
        $("#" + fieldId).attr("value", "");
    }

    function calculateContainmentObjectId(draggable) {
        var result = draggable.attr("id").substr(11);
        result = result.substr(0, result.indexOf("_"));
        result = "#associateQuestion_" + result;

        return result;
    }

    function onDropOut(dropTarget, draggable) {
        var dropTargetId = dropTarget.attr('id');
        var draggableId = draggable.attr('id');

        if (dropTargetId in droppedItems && droppedItems[dropTargetId] == draggableId) {
            dropTarget.removeClass("highlighted");
            resetFieldValue(dropTargetId + "_field");
            delete droppedItems[dropTargetId];
        }
    }

    function onDropOver(dropTarget, draggable) {
        var dropTargetId = dropTarget.attr('id');
        var draggableId = draggable.attr('id');

        if (!(dropTargetId in droppedItems)) {
            dropTarget.addClass("highlighted");
            setFieldValue(dropTargetId + "_field", getDraggableValue(draggableId));
            droppedItems[dropTargetId] = draggableId;
        }
    }

    function onDragStop(draggable) {
        var draggableId = draggable.attr("id");

        for (var dropTargetId in droppedItems) {
            if (droppedItems[dropTargetId] == draggableId) {
                putDraggableIntoDroppable(draggableId, dropTargetId);
            }
        }
    }

    function getDraggableValue(draggableId) {
        return $("#" + draggableId + " p").text();
    }

    function getDroppableValue(droppableId) {
        return $("#" + droppableId + "_field").attr("value");
    }

    function positionParticipants() {
        $(".associationCell input").each(function () {
            var droppableValue = $(this).val();
            var droppableId = $(this).parent(".associationCell").attr("id");

            if (droppableValue) {
                var draggableId = findMatchingDraggableIdByDroppableValue(droppableValue);
                putDraggableIntoDroppable(draggableId, droppableId);
                $("#" + droppableId).addClass("highlighted");
            }
        });
    }

    function findMatchingDraggableIdByDroppableValue(droppableValue) {

        var result = null;

        $(".participant").each(function () {
            var draggableId = $(this).attr("id");
            var participantValue = $(this).children('p').text();
            if (droppableValue == participantValue && !isAlreadyPlaced(draggableId)) {
                result = draggableId;
                //break out of loop
                return false;
            }
        });
        return result;
    }

    function isAlreadyPlaced(draggableId) {

        var result = false;

        for (var dropTargetId in droppedItems) {
            if (droppedItems[dropTargetId] == draggableId) {
                result = true;
                break;
            }
        }
        return result;
    }

    function putDraggableIntoDroppable(draggableId, droppableId) {
        $("#" + draggableId).position({my:"center center", at:"center center", of:$("#" + droppableId), collision:"none"});
        droppedItems[droppableId] = draggableId;
    }

}

