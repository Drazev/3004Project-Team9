/**
 * All outbound communication to the server goes through module.
 */
import client from "./clientSocket"

export const sendSponsorSearchResponse = (name, sponsorDecision) => {
    /**
     * Respond to a sponsor search request from the server
     */
    console.log(`Player name=${name} decides to ${sponsorDecision ? 'sponsor' : 'not sponsor'} the quest`);
    client.publish({
        destination: "/app/quest/sponsor-response",
        body: JSON.stringify({
            name: name,
            found: sponsorDecision
        })
    });
}

export const sendQuestJoinResponse = (name, joinDecision) => {
    /**
     * Respond to a quest stage join request from the server
     */
    console.log(`Player name=${name} decides to ${joinDecision ? 'join' : 'not join'} the the quest stage`);
    client.publish({
        destination: "/app/quest/join-response",
        body: JSON.stringify({
            name: name,
            joined: joinDecision
        })
    });
}

export const sendSponsorSetupCompleteResponse = (name, playerID, { setIsSponsoring }) => {
    console.log(`name=${name} playerID=${playerID}`);
    let response = await fetch("http://localhost:8080/api/quest/setup-complete", {
        method: "POST",
        body: JSON.stringify({
            name: name,
            playerID: playerID
        }),
        headers: {
            "Content-Type": "application/json"
        }
    })

    let body = await response.json();
    console.log(`Player name=${body.name} has finished setting up: ${body.confirmed}`);
    if (body.confirmed === true) {
        console.log("Sponsor request accepted")
        setIsSponsoring(false);
    }
}

export function sendParticipantSetupCompleteResponse(name, playerID) {
    console.log(`participantSetupComplete name=${name} playerID=${playerID}`);
    client.publish({
        destination: "/app/quest/participant-setup-complete",
        body: JSON.stringify({
            name: name,
            playerID: playerID
        })
    });
}

export const sendTargetSelectResponse = () => {
    /**
     * Respond to a quest stage join request from the server
     */
    console.log(`Player name=${name} decides to ${joinDecision ? 'join' : 'not join'} the the quest stage`);
    client.publish({
        destination: "/app/quest/join-response",
        body: JSON.stringify({
            name: name,
            joined: joinDecision
        })
    });
}
