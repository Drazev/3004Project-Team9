import { useTitle } from "../Store";

const Header = ({}) => {
  const title = useTitle();
  return (
    <div>
      <h1 className="h1">{title}</h1>
    </div>
  );
};

export default Header;
