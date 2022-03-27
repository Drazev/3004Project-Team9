/**
 * All inbound communication from the server goes through module.
 */
import client from "../services/clientSocket"

export const handleSponsorSearchRequest = (body, { setSponsorName }) => {
    console.log("/topic/quest/sponsor-search: " + JSON.stringify(body));
    setSponsorName(body.name);
}

export const handleQuestJoinRequest = (body, { setJoinRequest }) => {
    console.log("/topic/quest/join-request: " + JSON.stringify(body));
    setJoinRequest(true);
}

export const handleTargetSelectionRequest = (body, utilities) => {
}

