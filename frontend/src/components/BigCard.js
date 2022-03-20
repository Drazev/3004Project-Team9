import React,{useState} from "react";

function BigCard(props){
    const size = {width: "100px", height: "135px"};
    return (
        <div 
            id="CardSection"
            position="absolute"
            style={{height:68,width:73,margin:"0 auto",float:"left",marginBottom:10,marginLeft:35}}
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
