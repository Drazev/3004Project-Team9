import { effectRequestStore } from "../stores/effects/effectRequestStore";
/**
 * All effect inbound request from the server goes through module.
 */

export const dispatchTargetSelectionRequest = (body) => {
    /**
     * {
     *    requestID : 2,
     *    requestPlayerID: 123
     *    requestCardCode: "MERLIN",
     *    responseType:  "STAGE_TARGET_SELECTION" //Can also be CARD_TARGET_SELECTION for mordred
     * }
     */
    console.log("/topic/quest/target-selection: " + JSON.stringify(body));
    if (body.responseType === "STAGE_TARGET_SELECTION") {
        effectRequestStore().setStageTargetSelectionRequest(true);
        effectRequestStore().setStageTargetSelectionRequestBody(body);
    } else if (body.responseType === "CARD_TARGET_SELECTION") {
        effectRequestStore().setCardTargetSelectionRequest(true);
        effectRequestStore().setCardTargetSelectionRequestBody(body);
    } else {
        console.log("Unknown response type: " + body.responseType);
    }

}

