package views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controllers.ObterMetricas;
import models.LoginModel;
import models.SelectMaisDemoradasModel;
import java.awt.Toolkit;

public class SelectsMaisDemoradasView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;
	

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 * @param login 
	 */
	public SelectsMaisDemoradasView(LoginModel login) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(SelectsMaisDemoradasView.class.getResource("/img/Vigilant.png")));
		setTitle("Vigilant");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 850, 500);
		contentPane = new JPanel();
		contentPane.setForeground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"Data", "Query", "Tempo Total de execu\u00E7\u00E3o"
			}
		));
		scrollPane.setViewportView(table);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		contentPane.add(panel, BorderLayout.SOUTH);
		
		JButton btnMostrarTamanhoBancos = new JButton("Mostrar Dados");
		btnMostrarTamanhoBancos.setForeground(Color.WHITE);
		btnMostrarTamanhoBancos.setBackground(Color.BLACK);
		btnMostrarTamanhoBancos.setFont(new Font("Segoe UI", Font.BOLD, 19));
		btnMostrarTamanhoBancos.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<SelectMaisDemoradasModel> lista = new ArrayList<SelectMaisDemoradasModel>();
				ObterMetricas metricas = new ObterMetricas(login);
				lista = metricas.SelectMaisDemoradas();
				
				String query;
				String tempo;
				String data;
				
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				
				//Remove as linhas da pesquisa anterior
				int rowCount = model.getRowCount();
				for (int i = rowCount - 1; i >= 0; i--) {
				    model.removeRow(i);
				}
				
				//Adiciona as linhas do array retornado na tabela
				for(int i = 0; i < lista.size(); i++)
				{		
					query = lista.get(i).getQuery();
					tempo = lista.get(i).getTempo();
					data = lista.get(i).getData();
					
					model.addRow(new String[] {data, query,tempo});
				}
			}
		});
		panel.add(btnMostrarTamanhoBancos);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.DARK_GRAY);
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(panel_1, BorderLayout.NORTH);
		
		JButton btnNewButton = new JButton("");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Principal telaPrinc = new Principal(login);
				telaPrinc.setVisible(true);
				dispose();
			}
		});
		btnNewButton.setIcon(new ImageIcon(TamanhoBancosView.class.getResource("/img/seta-voltar.png")));
		btnNewButton.setPreferredSize(new Dimension(60, 23));
		btnNewButton.setMaximumSize(new Dimension(50, 23));
		panel_1.add(btnNewButton);
	}

}
