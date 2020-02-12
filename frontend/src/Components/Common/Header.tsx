import React, { ChangeEvent, useState, useEffect } from "react";
import "./Header.scss";
import logo from "../img/두근마켓3.png";
import logo2 from "../img/두근마켓2.png";

import { Link } from "react-router-dom";
import Hamburger from "./hamburger/Hamburger";

function Header() {
  const [inputText, setInputText] = useState(
    window.sessionStorage.getItem("searchText")
  );

  useEffect(() => {
    const isValid = window.sessionStorage.getItem("isText");
    if (isValid === "false") {
      setInputText("");
      window.sessionStorage.setItem("searchText", "");
    }
  }, []);

  const onChange = (e: ChangeEvent<HTMLInputElement>) => {
    setInputText(e.target.value);
  };

  const inputClick = () => {
    if (inputText) {
      window.sessionStorage.setItem("searchText", inputText);
    }
  };

  return (
    <header className="Header">
      <div className="div">
        <Hamburger></Hamburger>
        <Link to={{ pathname: "/" }}>
          <img className="Logo1" alt="logo" src={logo}></img>
          <img className="Logo2" alt="logo" src={logo2}></img>
        </Link>
        <input
          onChange={onChange}
          value={inputText || ""}
          className="input_search"
          id="input_search"
          type="text"
        ></input>
        <a href="/search" onClick={inputClick}>
          <button className="btn_search" type="button">
            <img
              className="img_search"
              alt="search"
              src="https://image.flaticon.com/icons/svg/711/711319.svg"
            ></img>
          </button>
        </a>
      </div>
    </header>
  );
}

export default Header;
