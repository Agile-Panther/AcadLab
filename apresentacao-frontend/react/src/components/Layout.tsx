import { NavLink, Outlet, useLocation } from 'react-router-dom';

const NAV_ITEMS = [
  { to: '/', icon: '📋', label: 'Gestão Curricular' },
  { to: '/periodo-letivo', icon: '📅', label: 'Período Letivo' },
  { to: '/oferta-turmas', icon: '👥', label: 'Oferta de Turmas' },
  { to: '/matricula', icon: '📝', label: 'Matrícula' },
  { to: '/gestao-pedagogica', icon: '📚', label: 'Gestão Pedagógica' },
  { to: '/historico-academico', icon: '📄', label: 'Histórico Acadêmico' },
  { to: '/secretaria-virtual', icon: '💬', label: 'Secretaria Virtual' },
  { to: '/integralizacao', icon: '🔄', label: 'Integralização' },
  { to: '/atividades-complementares', icon: '⭐', label: 'Ativ. Complementares' },
  { to: '/gestao-financeira', icon: '💰', label: 'Gestão Financeira' },
  { to: '/estagios', icon: '🏢', label: 'Estágios' },
];

function getPageInfo(pathname: string): { title: string; subtitle: string; initials: string } {
  if (pathname.startsWith('/secretaria-virtual/analise')) {
    return {
      title: 'Secretaria Virtual Acadêmica',
      subtitle: 'F-07 · Solicitações, Protocolos e Documentos',
      initials: 'US',
    };
  }
  if (pathname.startsWith('/secretaria-virtual')) {
    return {
      title: 'Secretaria Virtual Acadêmica',
      subtitle: 'F-07 · Estudante: Ana Carolina Silva — 2022001234',
      initials: 'AC',
    };
  }
  if (pathname.startsWith('/integralizacao')) {
    return {
      title: 'Integralização Curricular',
      subtitle: 'F-08 · Validação de Integralização e Colação de Grau',
      initials: 'IC',
    };
  }
  return {
    title: 'AcadLab',
    subtitle: 'Sistema Acadêmico Integrado',
    initials: 'US',
  };
}

export default function Layout() {
  const location = useLocation();
  const { title, subtitle, initials } = getPageInfo(location.pathname);

  return (
    <div className="layout">
      <aside className="sidebar">
        <div className="sidebar-header">
          <button className="sidebar-hamburger">☰</button>
          <span className="sidebar-logo">AcadLab</span>
        </div>
        <nav className="sidebar-nav">
          {NAV_ITEMS.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) => isActive ? 'active' : ''}
              end={item.to === '/'}
            >
              <span className="nav-icon">{item.icon}</span>
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>

      <div className="main-area">
        <header className="page-topbar">
          <div className="page-topbar-left">
            <h1>{title}</h1>
            <div className="subtitle">{subtitle}</div>
          </div>
          <div className="user-avatar">{initials}</div>
        </header>

        <div className="page-content">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
