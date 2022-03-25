
function BigCard(props){
    const size = {width: "100px", height: "135px"};
    return (
        <div 
            id="CardSection"
            style={{position:"absolute",height:68,width:73,marginBottom:10,marginLeft:40}}
        >
            {(props.numCards >= 0) &&
              <p style={{
                marginLeft: 27,
                marginBottom: 0
              }}>x{props.numCards}</p>
            }
          <img 
            id="CardImage"
            src={props.cardImage} 
            style={{
                width:size.width,
                height:size.height,
                borderRadius:10,
            }}
            alt="nonono"
          />
        </div>
      );
}

export default BigCard;
