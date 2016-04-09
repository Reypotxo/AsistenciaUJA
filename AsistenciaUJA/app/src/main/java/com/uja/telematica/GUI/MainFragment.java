/**
 * 
 */
package com.uja.telematica.GUI;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.uja.telematica.R;

/**
 * @author Alfonso Troyano Montes
 *
 */
public class MainFragment extends Fragment {

	private String usuarioId;
	private String clave;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
		
		TextView usuarioTextView = (TextView) rootView.findViewById(R.id.editTextUsuarioLogin);
		TextView claveTextView = (TextView) rootView.findViewById(R.id.editTextClaveLogin);
        Button aceptarBoton = (Button) rootView.findViewById(R.id.buttonAceptarLogin);
        aceptarBoton.setTransformationMethod(null);
		
		return rootView;
	}

	/**
	 * @return the usuarioId
	 */
	public String getUsuarioId() {
		return usuarioId;
	}

	/**
	 * @param usuarioId the usuarioId to set
	 */
	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}

	/**
	 * @return the clave
	 */
	public String getClave() {
		return clave;
	}

	/**
	 * @param clave the clave to set
	 */
	public void setClave(String clave) {
		this.clave = clave;
	}
	
	

}
