package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import models.LoginModel;
import models.SelectConflictsModel;
import models.SelectMaisDemoradasModel;
import models.SelectsChamadas1000xModel;
import models.SelectsMaisDemoradasMediaModel;
import models.TamanhoBancos;
import models.TamanhoTabelasModel;

public class ObterMetricas {
	private String driver = "org.postgresql.Driver";
	private String caminho;
	private String caminholite;
	private String porta;
	private String banco;
	private String usuario;
	private String senha;
	private Connection con;
	private Connection in;

	public ObterMetricas(LoginModel login) {
		usuario = login.getUsuario();
		senha = login.getSenha();
		banco = login.getBanco();
		porta = login.getPorta();

		caminho = "jdbc:postgresql://localhost:" + porta + "/" + banco;
		
		caminholite = "jdbc:sqlite:banco_de_dados/banco_sqlite.db";
	}

	public void iniciarConexao() throws SQLException {
		
		//System.setProperty("jdbc.drivers","org.postgresql.Driver:org.sqlite.JDBC" );		
		
		//Conex�o do PostgreSQL
		try {
			Class.forName("org.postgresql.Driver");
			//Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		
		try {
		con = DriverManager.getConnection(caminho, usuario, senha);
		}catch (SQLException ex) {
			System.out.println("Falha de Conex�o com PostgreSQL");
			ex.printStackTrace();
			throw ex;
		}
		
		//Conex�o do SQLite
		try {
		in = DriverManager.getConnection(caminholite);
		}catch (SQLException ex) {
			System.out.println("Falha de Conex�o com SQLite");
			ex.printStackTrace();
			throw ex;
		}
	}

	public void desconecta() {
		try {
			con.close();
			in.close();
		} catch (SQLException ex) {
		}
	}

	//# Select de Informa��es do Tamanho dos Bancos
	public ArrayList<TamanhoBancos> TamanhoBanco() {
		String sql = "SELECT pg_database.datname, pg_size_pretty(pg_database_size(pg_database.datname)),current_timestamp(0) AS size FROM pg_database;";		

		try {
			iniciarConexao();
			PreparedStatement pesquisa = con.prepareStatement(sql);
			ResultSet result = pesquisa.executeQuery();
			ArrayList<TamanhoBancos> lista = new ArrayList<TamanhoBancos>();

			while (result.next()) {
				TamanhoBancos tamBan = new TamanhoBancos();

				tamBan.setNome(result.getString("datname"));
				tamBan.setTamanho(result.getString("pg_size_pretty"));
				tamBan.setData(result.getString("size"));
				lista.add(tamBan);
			}
			return lista;
		} catch (SQLException e) {
			throw new SQLRunTimeException(e.getMessage(), e);
		} finally {
			desconecta();
		}
	}

	//# Select de Informa��es das Tabelas dos Bancos
	public ArrayList<TamanhoTabelasModel> TamanhoTabelas() {

		String sql = " SELECT tabela, pg_size_pretty(pg_total_relation_size(esq_tab)), current_timestamp(0) AS tamanho"
				+ "FROM (SELECT tablename AS tabela, schemaname||'.'||tablename AS esq_tab"
				+ "FROM pg_catalog.pg_tables"
				+ "WHERE schemaname NOT"
				+ "IN ('pg_catalog', 'information_schema', 'pg_toast') ) AS x"
				+ "ORDER BY pg_total_relation_size(esq_tab) DESC;";

		try {
			iniciarConexao();
			PreparedStatement pesquisa = con.prepareStatement(sql);
			ResultSet result = pesquisa.executeQuery();
			ArrayList<TamanhoTabelasModel> lista = new ArrayList<TamanhoTabelasModel>();

			String query = "INSERT INTO Users ("
				    + " tabela,"
				    + " pg_size_pretty,"
				    + " tamanho,"
				    + " lastname,"
				    + " companyname,"
				    + " email_addr,"
				    + " want_privacy ) VALUES ("
				    + "null, ?, ?, ?, ?, ?, ?)";
			
			//PreparedStatement st = in.prepareStatement(query);
			
			while (result.next()) {
				TamanhoTabelasModel tamTab = new TamanhoTabelasModel();

				tamTab.setNome(result.getString("tabela"));
				tamTab.setTamanhoTotal(result.getString("pg_size_pretty"));
				tamTab.setData(result.getString("tamanho"));
				lista.add(tamTab);
				
				/*st.setString(1, tamTab.getNome());
				st.setString(2, tamTab.getTamanhoTotal());
				st.setString(3, tamTab.getData());
				st.executeUpdate();*/
				
			}
			return lista;
		} catch (SQLException e) {
			throw new SQLRunTimeException(e.getMessage(), e);
		} finally {
			desconecta();
		}
	}

	public ArrayList<SelectsChamadas1000xModel> SelectsChamadas1000x() {

		String sql = "SELECT calls, SUBSTRING (query FROM 1 for 50), total_exec_time, current_timestamp (0)"
				+ " FROM pg_stat_statements where calls > 100;";

		try {
			iniciarConexao();
			PreparedStatement pesquisa = con.prepareStatement(sql);
			ResultSet result = pesquisa.executeQuery();
			ArrayList<SelectsChamadas1000xModel> lista = new ArrayList<SelectsChamadas1000xModel>();
			Double num;

			while (result.next()) {
				SelectsChamadas1000xModel selects = new SelectsChamadas1000xModel();
				selects.setCalls(result.getString("calls"));
				selects.setQuery(result.getString("substring"));
				selects.setDate(result.getString("current_timestamp"));
				DecimalFormat df = new DecimalFormat("####.00");
				num = Double.parseDouble(result.getString("total_exec_time"));
				selects.setTotal_exec_time(df.format(num).toString());
				lista.add(selects);
			}
			return lista;
		} catch (SQLException e) {
			throw new SQLRunTimeException(e.getMessage(), e);
		} finally {
			desconecta();
		}
	}

	public List<SelectMaisDemoradasModel> SelectMaisDemoradas() {

		String sql = "SELECT SUBSTRING (query FROM 1 for 50), total_exec_time, current_timestamp(0) \n"
				+ " FROM pg_stat_statements ORDER BY total_exec_time DESC LIMIT 10;";

		try {
			iniciarConexao();
			PreparedStatement pesquisa = con.prepareStatement(sql);
			ResultSet result = pesquisa.executeQuery();
			List<SelectMaisDemoradasModel> lista = new LinkedList<SelectMaisDemoradasModel>();
			Double num;

			while (result.next()) {
				SelectMaisDemoradasModel selects = new SelectMaisDemoradasModel();

				selects.setQuery(result.getString("substring"));
				selects.setData(result.getString("current_timestamp"));
				DecimalFormat df = new DecimalFormat("####.00");
				num = Double.parseDouble(result.getString("total_exec_time"));
				selects.setTempo(df.format(num).toString());
				lista.add(selects);
			}
			return lista;
		} catch (SQLException e) {
			throw new SQLRunTimeException(e.getMessage(), e);
		} finally {
			desconecta();
		}
	}

	public ArrayList<SelectsMaisDemoradasMediaModel> SelectsMaisDemoradasMedia() {

		String sql = "SELECT SUBSTRING (query FROM 1 for 50), mean_exec_time, current_timestamp (0) FROM pg_stat_statements ORDER BY "
				+ "mean_exec_time DESC LIMIT 10;";

		try {
			iniciarConexao();
			PreparedStatement pesquisa = con.prepareStatement(sql);
			ResultSet result = pesquisa.executeQuery();
			ArrayList<SelectsMaisDemoradasMediaModel> lista = new ArrayList<SelectsMaisDemoradasMediaModel>();
			Double num;

			while (result.next()) {

				SelectsMaisDemoradasMediaModel selects = new SelectsMaisDemoradasMediaModel();
				selects.setQuery(result.getString("substring"));
				selects.setData(result.getString("current_timestamp"));
				DecimalFormat df = new DecimalFormat("####.00");
				num = Double.parseDouble(result.getString("mean_exec_time"));
				selects.setTempoMedio(df.format(num).toString());
				lista.add(selects);
			}
			return lista;
		} catch (SQLException e) {
			throw new SQLRunTimeException(e.getMessage(), e);
		} finally {
			desconecta();
		}
	}
	
	public List<SelectConflictsModel> SelectConflicts() {

		String sql = "SELECT datname, confl_lock, confl_deadlock, current_timestamp (0) FROM pg_stat_database_conflicts;";

		try {
			iniciarConexao();
			PreparedStatement pesquisa = con.prepareStatement(sql);
			ResultSet result = pesquisa.executeQuery();
			List<SelectConflictsModel> lista = new LinkedList<SelectConflictsModel>();

			while (result.next()) {
				SelectConflictsModel selects = new SelectConflictsModel();

				selects.setName(result.getString("datname"));
				selects.setTempo(result.getString("current_timestamp"));
				selects.setConfl(result.getString("confl_lock"));
				selects.setConfl_dead(result.getString("confl_deadlock"));
				lista.add(selects);
			}
			return lista;
		} catch (SQLException e) {
			throw new SQLRunTimeException(e.getMessage(), e);
		} finally {
			desconecta();
		}
	}
}