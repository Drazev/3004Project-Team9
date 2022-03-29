/**
 * All event inbound request from the server goes through module.
 */

export const dispatchTargetSelectionRequest = (body, utilities) => {
    console.log("/topic/quest/target-selection: " + JSON.stringify(body));
}

