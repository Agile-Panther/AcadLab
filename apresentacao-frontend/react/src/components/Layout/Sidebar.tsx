import { NavLink } from 'react-router-dom';

const menuItems = [
  { path: '/gestao-curricular', label: 'Gestão Curricular' },
  { path: '/periodo-letivo', label: 'Período Letivo' },
  { path: '/oferta-turmas', label: 'Oferta de Turmas' },
  { path: '/matricula', label: 'Matrícula' },
  { path: '/gestao-pedagogica', label: 'Gestão Pedagógica' },
  { path: '/historico-academico', label: 'Histórico Acadêmico' },
  { path: '/secretaria-virtual', label: 'Secretaria Virtual' },
  { path: '/integralizacao', label: 'Integralização' },
  { path: '/ativ-complementares', label: 'Ativ. Complementares' },
  { path: '/gestao-financeira', label: 'Gestão Financeira' },
  { path: '/estagios', label: 'Estágios' },
];

export default function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar-logo">AcadLab</div>
      <nav className="sidebar-nav">
        {menuItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) => `sidebar-link${isActive ? ' active' : ''}`}
          >
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
