import { Link } from 'react-router-dom';

export default function Home() {
  return (
    <div className="page home-page">
      <h2>Bem-vindo ao AcadLab</h2>
      <p>Sistema Academico Integrado</p>

      <div className="home-cards">
        <Link to="/secretaria-virtual" className="home-card">
          <h3>Minhas Solicitacoes</h3>
          <p>Acompanhe o status das suas solicitacoes academicas</p>
        </Link>
        <Link to="/secretaria-virtual/nova" className="home-card">
          <h3>Nova Solicitacao</h3>
          <p>Abra uma nova solicitacao junto a secretaria</p>
        </Link>
        <Link to="/secretaria-virtual/analise" className="home-card">
          <h3>Painel da Secretaria</h3>
          <p>Analise as solicitacoes pendentes</p>
        </Link>
      </div>
    </div>
  );
}
