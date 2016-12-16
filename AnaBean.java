
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;


@ManagedBean
@RequestScoped
public class AnaBean 
{
    
    String dbUser="YourDBUser",dbPass="DBPassword";
    
    private double satilanMiktar,alinanMiktar,oran;
    private String uyeKodu,alinanDovizTur,satilanDovizTur;
    String komut,yuklenecekDovizTur;
    Boolean uyeVarMi=false,uygunMu,girisGorunur;
    double guncelTL,guncelDolar,guncelEuro,guncelSterlin,kurTL=1,kurDolar,kurSterlin,kurEuro,yuklenecekDovizMiktar,kodUSDMiktar,kodTLMiktar,kodEuroMiktar,kodSterlinMiktar;
    String YuklenecekTL;
    String bilgiDovizYukle,bilgiTakas;

    
    public Boolean getGirisGorunur() {
        
        if(new LoginBean().getSession().getAttribute("User")==null)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setGirisGorunur(Boolean girisGorunur) {
        this.girisGorunur = girisGorunur;
    }
    
    public String getBilgiTakas() {
        return bilgiTakas;
    }

    public void setBilgiTakas(String bilgiTakas) {
        this.bilgiTakas = bilgiTakas;
    }
        
    public boolean kodBilgiAl()
    {
        boolean var=false;
        Connection con=null;
        PreparedStatement pre=null;
        ResultSet rs=null;
        
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            con=DriverManager.getConnection("jdbc:mysql://localhost:3306/Doviz",dbUser,dbPass);
            pre=con.prepareStatement("Select * from Kodlar where kod='"+YuklenecekTL+"'");
            rs=pre.executeQuery();
            
            while(rs.next())
            {
                yuklenecekDovizMiktar=rs.getDouble("miktar");
                yuklenecekDovizTur=rs.getString("tip");
                uygunMu=rs.getBoolean("uygun");
                var=true;
            }
        }
        catch(Exception ex)
        {
            bilgiDovizYukle=ex.getMessage();
        }
        finally
        {
            try
            {
                con.close();
                pre.close();
                rs.close();
            }
            catch(Exception ex)
            {
            
            }
        }
        return var;
    }
    public boolean uyeBilgiAl()
    {
        boolean var=false;
        Connection con=null;
        PreparedStatement pre=null;
        ResultSet rs=null;
        
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            con=DriverManager.getConnection("jdbc:mysql://localhost:3306/Doviz",dbUser,dbPass);
            pre=con.prepareStatement("Select * from Uyeler where email='"+uyeKodu+"'");
            rs=pre.executeQuery();
            
            while(rs.next())
            {
                var=true;
                kodUSDMiktar=rs.getDouble("dolar");
                kodTLMiktar=rs.getDouble("lira");
                kodEuroMiktar=rs.getDouble("euro");
                kodSterlinMiktar=rs.getDouble("sterlin");
            }
        }
        catch(Exception ex)
        {
            bilgiDovizYukle=ex.getMessage();
        }
        finally
        {
            try
            {
                con.close();
                pre.close();
                rs.close();
            }
            catch(Exception ex)
            {
            
            }
        }
        return var;
    }
    public void DovizYuke(ActionEvent e)
    {
        if(kodBilgiAl() && uyeBilgiAl() && uygunMu)
        {
            kodBilgiAl();
            Connection con=null;
            PreparedStatement preUser=null;
            PreparedStatement preKod=null;
            int i=0,y=0;
            double yeni = 100;
            try
            {
                Class.forName("com.mysql.jdbc.Driver");
                con=DriverManager.getConnection("jdbc:mysql://localhost:3306/Doviz",dbUser,dbPass);
                
                if(yuklenecekDovizTur.equals("Dolar"))
                {
                    yeni=kodUSDMiktar+yuklenecekDovizMiktar;
                }
                else if(yuklenecekDovizTur.equals("Euro"))
                {
                    yeni=kodEuroMiktar+yuklenecekDovizMiktar;
                }
                else if(yuklenecekDovizTur.equals("Lira"))
                {
                    yeni=kodTLMiktar+yuklenecekDovizMiktar;
                }
                else if(yuklenecekDovizTur.equals("Sterlin"))
                {
                    yeni=kodSterlinMiktar+yuklenecekDovizMiktar;
                }
                preUser=con.prepareStatement("Update Uyeler SET "+yuklenecekDovizTur+"="+yeni+" where email='"+uyeKodu+"'");
                preKod=con.prepareStatement("Update Kodlar SET uygun=0 where kod='"+YuklenecekTL+"'");
                i=preUser.executeUpdate();
                y=preKod.executeUpdate();
                basarili(yuklenecekDovizMiktar+" "+yuklenecekDovizTur+" hesabınıza başarıyla aktarıldı!");
            }
            catch(Exception ex)
            {
                hata("Hata Var:"+ex.getMessage());
            }
            finally
            {
                try {
                    con.close();
                    preUser.close();
                    preKod.close();
                } catch (SQLException ex) {
                    Logger.getLogger(AnaBean.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        }
        else
        {
            hata("Girdiğiniz kod yada kullanıcı bilgileri hatalı!");
        }
        uygunMu=false;
      
    }
    
    
    @PostConstruct
    public void KurCek()
    {
        
        Connection con=null;
        PreparedStatement pre=null;
        ResultSet rs=null;
        
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            con=DriverManager.getConnection("jdbc:mysql://localhost:3306/Doviz",dbUser,dbPass);
            pre=con.prepareStatement("Select * from Kurlar");
            rs=pre.executeQuery();
            
            while(rs.next())
            {
                kurDolar=rs.getDouble("dolar");
                kurEuro=rs.getDouble("euro");
                kurSterlin=rs.getDouble("sterlin");
                System.out.print(kurDolar+":"+kurEuro+":"+kurSterlin);
            }
        }
        catch(Exception ex)
        {
        
        }
        finally
        {
            try
            {
                con.close();
                pre.close();
                rs.close();
            }
            catch(Exception ex)
            {

            }
        }
        
        Connection con2=null;
        PreparedStatement pre2=null;
        ResultSet rs2=null;
        
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            con2=DriverManager.getConnection("jdbc:mysql://localhost:3306/Doviz",dbUser,dbPass);
            pre2=con2.prepareStatement("Select * from Bilgi");
            rs2=pre2.executeQuery();
            
            while(rs2.next())
            {
                oran=rs2.getDouble("oran");
            }
        }
        catch(Exception ex)
        {
            
        }
        finally
        {
            try
            {
                con2.close();
                pre2.close();
                rs2.close();
            }
            catch(Exception ex)
            {
            
            }
        }
        
    }
    
    public void getMenkul(String email)
    {
        Connection con=null;
        PreparedStatement pre=null;
        ResultSet rs=null;
        
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            con=DriverManager.getConnection("jdbc:mysql://localhost:3306/Doviz",dbUser,dbPass);
            pre=con.prepareStatement("Select dolar,euro,sterlin,lira from Uyeler where email='"+email+"'");
            rs=pre.executeQuery();
            
            while(rs.next())
            {
                guncelTL=rs.getDouble("lira");
                guncelDolar=rs.getDouble("dolar");
                guncelEuro=rs.getDouble("euro");
                guncelSterlin=rs.getDouble("sterlin");
            }
        }
        catch(Exception ex)
        {
            bilgiTakas="Hata menkul çekme aşamasında";
        }
        finally
        {
            try
            {
                con.close();
                pre.close();
                rs.close();
            }
            catch(Exception ex)
            {
                
            }
            
        }
    }

    public String getBilgiDovizYukle() {
        return bilgiDovizYukle;
    }

    public void setBilgiDovizYukle(String bilgiDovizYukle) {
        this.bilgiDovizYukle = bilgiDovizYukle;
    }
    
    public double getKurDolar() {
        return kurDolar;
    }

    public void setKurDolar(double kurDolar) {
        this.kurDolar = kurDolar;
    }

    public double getKurSterlin() {
        return kurSterlin;
    }

    public void setKurSterlin(double kurSterlin) {
        this.kurSterlin = kurSterlin;
    }

    public double getKurEuro() {
        return kurEuro;
    }

    public void setKurEuro(double kurEuro) {
        this.kurEuro = kurEuro;
    }

    public double getSatilanMiktar() {
        return satilanMiktar;
    }

    public void setSatilanMiktar(double satilanMiktar) {
        this.satilanMiktar = satilanMiktar;
    }

    public double getAlinanMiktar() {
        return alinanMiktar;
    }

    public void setAlinanMiktar(double alinanMiktar) {
        this.alinanMiktar = alinanMiktar;
    }

    public String getAlinanDovizTur() {
        return alinanDovizTur;
    }

    public void setAlinanDovizTur(String alinanDovizTur) {
        this.alinanDovizTur = alinanDovizTur;
    }

    public String getSatilanDovizTur() {
        return satilanDovizTur;
    }

    public void setSatilanDovizTur(String satilanDovizTur) {
        this.satilanDovizTur = satilanDovizTur;
    }
   
    public String getYuklenecekTL() {
        return YuklenecekTL;
    }

    public void setYuklenecekTL(String YuklenecekTL) {
        this.YuklenecekTL = YuklenecekTL;
    }

    public String getUyeKodu() {
        return uyeKodu;
    }

    public void setUyeKodu(String uyeKodu) {
        this.uyeKodu = uyeKodu;
    }
  
    
    public void GuncelDovizCek(String tc)
    {
        if(satilanDovizTur=="Dolar")
        {
            komut="Select Dolar from UYELER where tc='"+tc+"'";
        }
        else if(satilanDovizTur=="Euro")
        {
            komut="Select Euro from UYELER where tc='"+tc+"'";

        }
        else if(satilanDovizTur=="Sterlin")
        {
            komut="Select Sterlin from UYELER where tc='"+tc+"'";

        }
        else if(satilanDovizTur=="Lira")
        {
            komut="Select Lira from UYELER where tc='"+tc+"'";

        }
    }
    
    public void Al(ActionEvent e)
    {
        if(new LoginBean().getSession().getAttribute("User")==null)
        {
            hata("Giriş Yapmadınız!");
        }
        else
        {
            DovizAL(new LoginBean().getSession().getAttribute("User").toString());
        }
    }
    
    public void DovizAL(String email)
    {
        getMenkul(email);
        KurCek();
        String komut="";
        double alinacak = 0;
        if(satilanDovizTur.equals("Dolar"))
        {
            if(alinanDovizTur.equals("Lira"))
            {
                alinacak=alinanMiktar/kurDolar*((100+oran)/100);
                komut="Update Uyeler SET lira="+(alinanMiktar+guncelTL)+",dolar="+(guncelDolar-alinacak)+" where email='"+email+"'";
            }
            else if(alinanDovizTur.equals("Euro"))
            {
                alinacak=alinanMiktar*kurEuro/kurDolar*((100+oran)/100);
                
                komut="Update Uyeler SET euro="+(alinanMiktar+guncelEuro)+",dolar="+(guncelDolar-alinacak)+" where email='"+email+"'";

            } 
            else if(alinanDovizTur.equals("Sterlin"))
            {
                alinacak=alinanMiktar*kurSterlin/kurDolar*((100+oran)/100);
                komut="Update Uyeler SET sterlin="+(alinanMiktar+guncelSterlin)+",dolar="+(guncelDolar-alinacak)+" where email='"+email+"'";

            }
            else if(alinanDovizTur.equals("Dolar"))
            {
                // Uyarı
            }
            
            if(guncelDolar>=alinacak)
            {
                DovizTakasi(komut);
            }
            else
            {
                hata("Döviz değişimi için yeterli dolarınız yok!");
            }
            
        }
        else if(satilanDovizTur.equals("Euro"))
        {
            if(alinanDovizTur.equals("Lira"))
            {
                alinacak=alinanMiktar/kurEuro*((100+oran)/100);
                komut="Update Uyeler SET lira="+(alinanMiktar+guncelTL)+",euro="+(guncelEuro-alinacak)+" where email='"+email+"'";
            }
            else if(alinanDovizTur.equals("Euro"))
            {
                // Uyarı
            } 
            else if(alinanDovizTur.equals("Sterlin"))
            {
                alinacak=alinanMiktar*kurSterlin/kurEuro*((100+oran)/100);
                komut="Update Uyeler SET sterlin="+(alinanMiktar+guncelSterlin)+",euro="+(guncelEuro-alinacak)+" where email='"+email+"'";
            }
            else if(alinanDovizTur.equals("Dolar"))
            {
                alinacak=alinanMiktar*kurDolar/kurEuro*((100+oran)/100);
                komut="Update Uyeler SET dolar="+(alinanMiktar+guncelDolar)+",euro="+(guncelEuro-alinacak)+" where email='"+email+"'";
            }
            
             if(guncelEuro>=alinacak)
            {
                DovizTakasi(komut);
            }
            else
            {
                hata("Döviz değişimi için yeterli euronuz yok!");
            }
        } 
        else if(satilanDovizTur.equals("Lira"))
        {
            if(alinanDovizTur.equals("Lira"))
            {
                // Uyarı
            }
            else if(alinanDovizTur.equals("Euro"))
            {
                alinacak=alinanMiktar*kurEuro*((100+oran)/100);
                komut="Update Uyeler SET euro="+(alinanMiktar+guncelEuro)+",lira="+(guncelTL-alinacak)+" where email='"+email+"'";
            } 
            else if(alinanDovizTur.equals("Sterlin"))
            {
                alinacak=alinanMiktar*kurSterlin*((100+oran)/100);
                komut="Update Uyeler SET sterlin="+(alinanMiktar+guncelSterlin)+",lira="+(guncelTL-alinacak)+" where email='"+email+"'";

            }
            else if(alinanDovizTur.equals("Dolar"))
            {
                alinacak=alinanMiktar*kurDolar*((100+oran)/100);
                komut="Update Uyeler SET dolar="+(alinanMiktar+guncelDolar)+",lira="+(guncelTL-alinacak)+" where email='"+email+"'";
            }
            
             if(guncelTL>=alinacak)
            {
                DovizTakasi(komut);
            }
            else
            {
                hata("Döviz değişimi için yeterli liranız yok!");
            }
        } 
        else if(satilanDovizTur.equals("Sterlin"))
        {
            if(alinanDovizTur.equals("Lira"))
            {
                alinacak=alinanMiktar/kurSterlin*((100+oran)/100);
                komut="Update Uyeler SET lira="+(alinanMiktar+guncelTL)+",sterlin="+(guncelSterlin-alinacak)+" where email='"+email+"'";
            }
            else if(alinanDovizTur.equals("Euro"))
            {
                alinacak=alinanMiktar*kurEuro/kurSterlin*((100+oran)/100);
                komut="Update Uyeler SET euro="+(alinanMiktar+guncelEuro)+",sterlin="+(guncelSterlin-alinacak)+" where email='"+email+"'";

            } 
            else if(alinanDovizTur.equals("Sterlin"))
            {
                // Uyarı
            }
            else if(alinanDovizTur.equals("Dolar"))
            {
                alinacak=alinanMiktar*kurDolar/kurSterlin*((100+oran)/100);
                komut="Update Uyeler SET dolar="+(alinanMiktar+guncelDolar)+",sterlin="+(guncelSterlin-alinacak)+" where email='"+email+"'";

            }
            if(guncelSterlin>=alinacak)
            {
                DovizTakasi(komut);
            }
            else
            {
                hata("Döviz değişimi için yeterli sterlininiz yok!");
            }
        } 
    }
    
    public void DovizTakasi(String komut)
    {
        Connection con=null;
        PreparedStatement pre=null;
        int i=0;
        
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            con=DriverManager.getConnection("jdbc:mysql://localhost:3306/Doviz",dbUser,dbPass);
            pre=con.prepareStatement(komut);
            i=pre.executeUpdate();
            basarili("Döviz Değişimi Başarılı!");
        }
        catch(Exception ex)
        {
            hata("Hata takas aşamasında");
        }
        finally
        {
            try {
                con.close();
                pre.close();
            } catch (SQLException ex) {
                Logger.getLogger(AnaBean.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void hata(String msj)
    {           
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Hata!", msj);    
        RequestContext.getCurrentInstance().showMessageInDialog(message);
    }
    public void basarili(String msj)
    {           
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Başarılı!", msj);
        RequestContext.getCurrentInstance().showMessageInDialog(message);
    }
}
