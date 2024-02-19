/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package code;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.ArticleOperations;

/**
 *
 * @author Admin
 */
public class _ArticleOperations implements ArticleOperations{

    @Override
    public int createArticle(int i, String string, int i1) {
        int idA;
        try(
            PreparedStatement stmt2 = DB.getInstance().getConnection().prepareStatement("select * from Shop where IdS = ?");
                ){
            stmt2.setInt(1, i);
            ResultSet rs = stmt2.executeQuery();
            if(! rs.next())
                return -1;
            
        } catch (SQLException ex) {
            Logger.getLogger(_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        try(PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("insert into Article(Name) values (?)", Statement.RETURN_GENERATED_KEYS);
                
            PreparedStatement stmt2 = DB.getInstance().getConnection().prepareStatement("insert into HasArticles(IdA, IdS, Price, Amount)\n" +
"values (?, ?, ?, 0)");
                ){
            stmt1.setString(1, string);
            stmt1.executeUpdate();
            ResultSet rs= stmt1.getGeneratedKeys();
            rs.next();
            idA = rs.getInt(1);
            
            stmt2.setInt(1, idA);
            stmt2.setInt(2, i);
            stmt2.setInt(3, i1);
            stmt2.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(_ArticleOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        return idA;
    }


    public static void main(String[] args) {
        _ArticleOperations A = new _ArticleOperations();
        System.out.println(A.createArticle(1, "Tv", 2000));
        System.out.println(A.createArticle(2, "Telefon", 1000));
        System.out.println(A.createArticle(2, "Tastatura", 3000));
        System.out.println(A.createArticle(3, "Mis", 4000));
        System.out.println(A.createArticle(4, "Slusalice", 500));
    }
}
