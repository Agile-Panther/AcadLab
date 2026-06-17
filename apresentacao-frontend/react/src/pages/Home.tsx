import { Link } from 'react-router-dom';

export default function Home() {
  return (
    <div className="home-page">
      <h2>Bem-vindo ao AcadLab</h2>
      <p className="home-subtitle">Sistema Acadêmico Integrado</p>

      <div className="home-cards">
        <Link to="/secretaria-virtual" className="home-card">
          <h3>Minhas Solicitações</h3>
          <p>Acompanhe o status das suas solicitações acadêmicas</p>
        </Link>
        <Link to="/secretaria-virtual/analise" className="home-card">
          <h3>Painel da Secretaria</h3>
          <p>Analise e gerencie as solicitações acadêmicas</p>
        </Link>
      </div>
    </div>
  );
}
