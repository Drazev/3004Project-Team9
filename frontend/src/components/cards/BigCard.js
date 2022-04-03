import { stageTargetSelectionRepsonse } from "../../services/clientSocket";
import { useStageTargetSelectionRequestBody, useSetStageTargetSelectionRequestBody, useSetStageTargetSelectionRequest } from "../../stores/effects/effectRequestStore";

import "./BigCard.css";


function BigCard({ numCards, cardId, cardImage, flippable, stageNum}) {
    const size = { width: "100px", height: "135px" };
    const [requestBody, setRequestBody] = [useStageTargetSelectionRequestBody(), useSetStageTargetSelectionRequestBody()];
    const setStageTargetSelectionRequest = useSetStageTargetSelectionRequest();

    const requestFlip = () => {
        console.log("Requesting flip");
        stageTargetSelectionRepsonse(requestBody.requestID, requestBody.requestPlayerID, stageNum);
        setStageTargetSelectionRequest(false);
        setRequestBody({});
    }

    return (
        <div
            id="CardSection"
            style={{ position: "absolute", height: 68, width: 73, marginBottom: 10, marginLeft: 40, float: "left" }}
        >
            {(numCards >= 0) &&
                <p style={{
                    marginLeft: 27,
                    marginBottom: 0
                }}>x{numCards}</p>
            }
            <img
                id="CardImage"
                src={cardImage}
                style={{
                    width: size.width,
                    height: size.height,
                    borderRadius: 10,
                }}
                alt="nonono"
            />
            {
                flippable &&
                <button onClick={requestFlip}>Flip</button>
            }
        </div>
    );
}

export default BigCard;
