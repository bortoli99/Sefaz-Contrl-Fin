package db;

import bean.MovimentoBean;
import models.Movimento;

import javax.faces.context.FacesContext;
import java.sql.*;
import java.util.*;
import java.sql.Date;

/**
 * Created by narut on 16/03/2021.
 */
public class MovimentosDAO {

    public static Statement stmtObj;
    public static Connection conn;
    public static ResultSet resultSetObj;
    public static PreparedStatement pstmt;
    public static final String DRIVER = "com.mysql.jdbc.Driver";

    public static Connection getConnection(){
        try {
            Class.forName(DRIVER);
            String db_url ="jdbc:mysql://localhost:3306/teste_sefaz",
                    db_userName = "root",
                    db_password = "";
            conn = DriverManager.getConnection(db_url,db_userName,db_password);;
        }catch (Exception sqlErr){
            sqlErr.printStackTrace();
        }

        return conn;
    }

    public static ArrayList getStudentsListFromDB() {
        ArrayList studentsList = new ArrayList();
        try {
            stmtObj = getConnection().createStatement();
            resultSetObj = stmtObj.executeQuery("select * from student_record");
            while(resultSetObj.next()) {
                Movimento stuObj = new Movimento();
                stuObj.setId(resultSetObj.getInt("student_id"));
                stuObj.setDescricao(resultSetObj.getString("student_name"));
                stuObj.setStatus(resultSetObj.getBoolean("student_email"));
                stuObj.setTipo(resultSetObj.getString("student_password"));
                stuObj.setValor(resultSetObj.getDouble("student_gender"));
                stuObj.setVencimento(resultSetObj.getString("student_address"));
                studentsList.add(stuObj);
            }
            System.out.println("Total Records Fetched: " + studentsList.size());
            conn.close();
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        return studentsList;
    }

    public static String saveStudentDetailsInDB(MovimentoBean newStudentObj) {
        int saveResult = 0;
        String navigationResult = "";
        try {
            pstmt = getConnection().prepareStatement("insert into movimento (descricao, tipo, valor, vencimento, status) values (?, ?, ?, ?, ?)");
            pstmt.setString(1, newStudentObj.getDescricao());
            pstmt.setString(2, newStudentObj.getTipo());
            pstmt.setString(3, newStudentObj.getValor());
            pstmt.setString(4, newStudentObj.getVencimento());
            pstmt.setBoolean(5, newStudentObj.isStatus());
            saveResult = pstmt.executeUpdate();
            conn.close();
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        if(saveResult !=0) {
            navigationResult = "studentsList.xhtml?faces-redirect=true";
        } else {
            navigationResult = "createStudent.xhtml?faces-redirect=true";
        }
        return navigationResult;
    }

    public static String editStudentRecordInDB(int studentId) {
        Movimento editRecord = null;
        System.out.println("editStudentRecordInDB() : Student Id: " + studentId);

        /* Setting The Particular Student Details In Session */
        Map<String,Object> sessionMapObj = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        try {
            stmtObj = getConnection().createStatement();
            resultSetObj = stmtObj.executeQuery("select * from movimento where id = "+studentId);
            if(resultSetObj != null) {
                resultSetObj.next();
                editRecord = new Movimento();
                editRecord.setId(resultSetObj.getInt("id"));
                editRecord.setVencimento(resultSetObj.getString("vencimento"));
                editRecord.setValor(resultSetObj.getDouble("valor"));
                editRecord.setStatus(resultSetObj.getBoolean("status"));
                editRecord.setDescricao(resultSetObj.getString("descricao"));
                editRecord.setTipo(resultSetObj.getString("tipo"));
            }
            sessionMapObj.put("editRecordObj", editRecord);
            conn.close();
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        return "/editStudent.xhtml?faces-redirect=true";
    }

    public static String updateStudentDetailsInDB(Movimento updateStudentObj) {
        try {
            pstmt = getConnection().prepareStatement("update movimento set descricao=?, tipo=?, vencimento=?, valor=?, satatus=? where id=?");
            pstmt.setString(1,updateStudentObj.getDescricao());
            pstmt.setString(2,updateStudentObj.getTipo());
            pstmt.setString(3,updateStudentObj.getVencimento());
            pstmt.setDouble(4,updateStudentObj.getValor());
            pstmt.setBoolean(5,updateStudentObj.isStatus());
            pstmt.setInt(6,updateStudentObj.getId());
            pstmt.executeUpdate();
            conn.close();
        } catch(Exception sqlException) {
            sqlException.printStackTrace();
        }
        return "/studentsList.xhtml?faces-redirect=true";
    }

    public static String deleteStudentRecordInDB(int studentId){
        System.out.println("deleteStudentRecordInDB() : Student Id: " + studentId);
        try {
            pstmt = getConnection().prepareStatement("delete from movimento where id = "+studentId);
            pstmt.executeUpdate();
            conn.close();
        } catch(Exception sqlException){
            sqlException.printStackTrace();
        }
        return "/studentsList.xhtml?faces-redirect=true";
    }

}
