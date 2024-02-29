import React from "react";

const MainHeader = () => {
    return (
        <header className="header-banner" style={{ backgroundColor: "#123456" }}>
            <div className="overlay"></div>
            <div className="animated-texts overlay-content">
                <h1>
                     <span className="hotel-color" > RoomRental</span>
                </h1>
                <h4 >Experience outstanding service</h4>
            </div>
        </header>
    );
};

export default MainHeader;
