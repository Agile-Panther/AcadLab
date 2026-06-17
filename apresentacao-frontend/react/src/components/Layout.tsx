import { NavLink, Outlet } from 'react-router-dom';

export default function Layout() {
  return (
    <div className="layout">
      <header className="topbar">
        <h1 className="topbar-title">AcadLab</h1>
        <nav className="topbar-nav">
          <NavLink to="/" end>Inicio</NavLink>
          <NavLink to="/secretaria-virtual">Minhas Solicitacoes</NavLink>
          <NavLink to="/secretaria-virtual/nova">Nova Solicitacao</NavLink>
          <NavLink to="/secretaria-virtual/analise">Painel da Secretaria</NavLink>
        </nav>
      </header>
      <main className="main-content">
        <Outlet />
      </main>
    </div>
  );
}
