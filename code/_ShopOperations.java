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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.ShopOperations;

/**
 *
 * @author Admin
 */
public class _ShopOperations implements ShopOperations{

    @Override
    public int createShop(String string, String string1) {
        int idG, idP;
        try(PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("select * from Shop where Name = ?");
            PreparedStatement stmt2 = DB.getInstance().getConnection().prepareStatement("select IdC from City where Name = ?");
                
                ){
            stmt1.setString(1, string);
            ResultSet rs  = stmt1.executeQuery();
            if(rs.next())
                return -1;
            stmt2.setString(1, string1);
            rs = stmt2.executeQuery();
            if(! rs.next())
                return -1;
            idG = rs.getInt(1);
            
        } catch (SQLException ex) {
            Logger.getLogger(_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        
        try(PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("insert into Shop(IdC, Balance, Name, Discount) values (?, 0, ?, 0)", Statement.RETURN_GENERATED_KEYS);
                ){
            stmt1.setInt(1, idG);
            stmt1.setString(2, string);
            stmt1.executeUpdate();
            ResultSet rs = stmt1.getGeneratedKeys();
            rs.next();
            idP = rs.getInt(1);
            
        } catch (SQLException ex) {
            Logger.getLogger(_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        
        return idP;
    }

    @Override
    public int setCity(int i, String string) {
        int idg;
        try(PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("select IdC from City where Name = ?");
            PreparedStatement stmt2 = DB.getInstance().getConnection().prepareStatement("update Shop set IdC = ? where IdS = ?");
                ){
            stmt1.setString(1, string);
            ResultSet rs = stmt1.executeQuery();
            if(!rs.next())
                return -1;
            idg = rs.getInt(1);
            stmt2.setInt(1, idg);
            stmt2.setInt(2, i);
            if(stmt2.executeUpdate() == 0)
                return -1;
            
        } catch (SQLException ex) {
            Logger.getLogger(_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 1;
    }

    @Override
    public int getCity(int i) {
        int idg;
        try(
            PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select IdC from Shop where IdS = ?");
                ){
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next())
                return -1;
            idg = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return idg;
    }

    @Override
    public int setDiscount(int i, int i1) {
        if (i1>100 || i1 < 0)
            return -1;
        try(
            PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("update Shop set Discount = ? where IdS = ?");
                ){
            stmt.setInt(1, i1);
            stmt.setInt(2, i);
            if(stmt.executeUpdate() == 0)
                return -1;
        } catch (SQLException ex) {
            Logger.getLogger(_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 1;
    }

    @Override
    public int increaseArticleCount(int i, int i1) {
        int kolicina=0;
        try(
            PreparedStatement stmt2 = DB.getInstance().getConnection().prepareStatement("update HasArticles set Amount = Amount + ? where IdA = ?");
                PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("select Amount from HasArticles where IdA = ?");
                ){
            
            stmt1.setInt(1, i);
            ResultSet rs =  stmt1.executeQuery();
            if(!rs.next())
                return -1;
            kolicina = rs.getInt(1) + i1;
            stmt2.setInt(1, i1);
            stmt2.setInt(2, i);
            if(stmt2.executeUpdate() == 0)
                return -1;
            
            
        } catch (SQLException ex) {
            Logger.getLogger(_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return kolicina;
    }

    @Override
    public int getArticleCount(int i) {
        int cnt = -1;
        try(
            PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select Amount from HasArticles where IdA = ?");
                ){
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next())
                return -1;
            cnt = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cnt;
    }

    @Override
    public List<Integer> getArticles(int i) {
        List<Integer> lista = new ArrayList<>();
        
        try(PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select IdA from HasArticles where IdS = ?");
                ){
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
                lista.add(rs.getInt(1));
        } catch (SQLException ex) {
            Logger.getLogger(_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return lista;
    }

    @Override
    public int getDiscount(int i) {
        int cnt = -1;
        try(
            PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select Discount from Shop where IdS = ?");
                ){
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next())
                return -1;
            cnt = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(_ShopOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return cnt;
    }
    
    
    public static void main(String[] args) {
        _ShopOperations S = new _ShopOperations();
        System.out.println(S.createShop("Kaa", "Kragujevac"));
        System.out.println(S.createShop("Kbb", "Kragujevac"));
        System.out.println(S.createShop("Naa", "Nis"));
        System.out.println(S.createShop("Nbb", "Nis"));
        //System.out.println(S.setCity(2, "Nis"));
        //System.out.println(S.getCity(2));
        //System.out.println(S.setDiscount(2, 10));
        System.out.println(S.increaseArticleCount(1, 20));
        System.out.println(S.increaseArticleCount(2, 20));
        System.out.println(S.increaseArticleCount(3, 20));
        System.out.println(S.increaseArticleCount(4, 20));
        //System.out.println(S.getArticleCount(2));
        //System.out.println(S.getArticles(40));
        //System.out.println(S.getDiscount(10));
    }
}
