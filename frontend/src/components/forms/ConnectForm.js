import { connect } from "../../services/clientSocket";
import { useName, useSetName } from "../../stores/generalStore";
import "./ConnectForm.css";

const ConnectForm = () => {
    const [name, setName] = [useName(), useSetName()];
    // Preprocess before making connection request
    const handleSubmit = (e) => {
        e.preventDefault();
        if (!name) {
            alert("Please enter your name");
            return;
        }
        if (connect() === false) {
            alert("Name already taken, please choose a different one")
        }
    };

    return (
        <form className="ConnectForm" onSubmit={handleSubmit}>
            <div>
                <input
                    type="text"
                    className="NameInput"
                    placeholder="Enter your name"
                    value={name}
                    onChange={(e) => {
                        setName(e.target.value);
                    }}
                />
            </div>
            <button type="submit" className="ConnectButton">
                Connect
            </button>
        </form>
    );
};

export default ConnectForm;
