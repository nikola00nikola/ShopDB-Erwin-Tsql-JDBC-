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
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author Admin
 */
public class _CityOperations implements CityOperations{

    @Override
    public int createCity(String string) {
        int idG;
        try(PreparedStatement stmt1 = DB.getInstance().getConnection().prepareStatement("select * from City where Name = ?");
            PreparedStatement stmt2 = DB.getInstance().getConnection().prepareStatement("insert into City(Name) values (?)", Statement.RETURN_GENERATED_KEYS);
                ){
            stmt1.setString(1, string);
            ResultSet rs = stmt1.executeQuery();
            if(rs.next())
                return -1;
            stmt2.setString(1, string);
            stmt2.executeUpdate();
            rs = stmt2.getGeneratedKeys();
            rs.next();
            idG = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return idG;
    }

    @Override
    public List<Integer> getCities() {
        List<Integer> lista = new ArrayList<>();
        
        try(Statement stmt = DB.getInstance().getConnection().createStatement();
                ){
            ResultSet rs =  stmt.executeQuery("select IdC from City");
            while(rs.next())
                lista.add(rs.getInt(1));
        } catch (SQLException ex) {
            Logger.getLogger(_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(lista.isEmpty())
            return null;
        return lista;
    }

    @Override
    public int connectCities(int i, int i1, int i2) {
        if(i == i1)
            return -1;
        if(i2 <= 0)
            return -1;
        int idL = -1;
        try(PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("insert into Line(IdC1, IdC2, Distance) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            PreparedStatement stmtprovera = DB.getInstance().getConnection().prepareStatement("select * from Line where IdC1 = ? and IdC2 = ?");
                ){
            stmtprovera.setInt(1, i);
            stmtprovera.setInt(2, i1);
            ResultSet rs = stmtprovera.executeQuery();
            if(rs.next())
                return -1;
            stmtprovera.setInt(1, i1);
            stmtprovera.setInt(2, i);
            rs = stmtprovera.executeQuery();
            if(rs.next())
                return -1;
            
            
            stmt.setInt(1, i);
            stmt.setInt(2, i1);
            stmt.setInt(3, i2);
            
            if(stmt.executeUpdate() == 0)
                return -1;
            rs = stmt.getGeneratedKeys();
            rs.next();
            idL = rs.getInt(1);
        } catch (SQLException ex) {
            Logger.getLogger(_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return idL;
    }

    @Override
    public List<Integer> getConnectedCities(int i) {
        ArrayList<Integer> lista = new ArrayList<>();
        try(PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select IdC1 from Line where IdC2 = ?");
             PreparedStatement stmt2 = DB.getInstance().getConnection().prepareStatement("select IdC2 from Line where IdC1 = ?");
                ){
            stmt.setInt(1, i);
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
                lista.add(rs.getInt(1));
            stmt2.setInt(1, i);
            rs = stmt2.executeQuery();
            while(rs.next())
                lista.add(rs.getInt(1));
            
        } catch (SQLException ex) {
            Logger.getLogger(_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }

    @Override
    public List<Integer> getShops(int i) {
        ArrayList<Integer> lista = new ArrayList<>();
        try(PreparedStatement stmt = DB.getInstance().getConnection().prepareStatement("select * from City where IdC = ?");
            PreparedStatement stmt2 = DB.getInstance().getConnection().prepareStatement("select IdS from Shop where IdC = ?");
                ){
            stmt.setInt(1, i);
            ResultSet rs= stmt.executeQuery();
            if(!rs.next())
                return null;
            stmt2.setInt(1, i);
            rs = stmt2.executeQuery();
            while(rs.next())
                lista.add(rs.getInt(1));
            
        } catch (SQLException ex) {
            Logger.getLogger(_CityOperations.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lista;
    }
    
    public static void main(String[] args) {
        _CityOperations C = new _CityOperations();
        System.out.println(C.createCity("Kragujevac"));
        System.out.println(C.createCity("Nis"));
        System.out.println(C.createCity("Beograd"));
        //System.out.println(C.getCities());
        System.out.println(C.connectCities(1,2, 5));
        System.out.println(C.connectCities(3,2, 10));
        System.out.println(C.connectCities(1,3, 2));
        //System.out.println(C.getShops(1));
        
    }
}
