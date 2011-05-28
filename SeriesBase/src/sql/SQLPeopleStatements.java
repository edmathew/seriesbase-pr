package sql;

/**
 * This class has a compilation of several static functions that returns SQL
 * statements to be used in the seriesbase database. The statements refer to all
 * the persons stored in the database.
 * 
 * @author Edgar Mateus
 * @author Nuno Dias
 * @author Tiago Amaral
 * 
 * @version May 2011
 */
public class SQLPeopleStatements {

	/**
	 * SQL statement to select all the persons stored in the database.
	 * 
	 * @return SQL Statement
	 */
	public static String getAllPersons() {
		return "SELECT * FROM Pessoa";
	}
	
	/**
	 * SQL select statement to retrieve a persons id by it's name.
	 * @return SQL Prepared Statement
	 */
	public static String getPersonId(){
		return "SELECT idPessoa FROM Pessoa WHERE Nome = ?";
	}
	
	
	

}
