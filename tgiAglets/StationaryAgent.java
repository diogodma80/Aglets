package tgiAglets;

/**
 * Created on 10/07/2009
 */

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import com.ibm.aglet.Aglet;
import com.ibm.aglet.AgletContext;
import com.ibm.aglet.AgletException;
import com.ibm.aglet.AgletProxy;

public class StationaryAgent extends Aglet {

    public void onCreation(Object o) {
        AgletContext context = getAgletContext();

        if (BD.getConnection()) // if conection ok
        {
            try {
                //string sql
                String sql = "SELECT ipDestino FROM IP";
                BD.setResultSet(sql);
                while (BD.resultSet.next()) {
                    String arg = BD.resultSet.getString("ipDestino");
                    AgletProxy proxy = context.createAglet(getCodeBase(), "tgiAglets.RemoteAgent", arg);
                    String ipDestino = BD.resultSet.getString("ipDestino");
                    proxy.dispatch(new URL("atp://" + ipDestino));
                    proxy = null;
                } //fim while BD.resultSet

                BD.close();

            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            } catch (InstantiationException ie) {
                ie.printStackTrace();
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (AgletException ae) {
                ae.printStackTrace();
            } finally {
				//destroi o agente estácionário
            	dispose();
            }
        }
    }

    public void run() {
        setText("Stationary Agent");
    }
}