package Views;

import java.sql.Connection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;


//import com.formdev.flatlaf.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

import DAOs.FactoryDAO;
import DAOs.MatchDAO;
import DAOs.TournoiDAO;
import Models.Equipe;
import Models.Match;
import Models.Tournoi;

import javax.swing.UIManager;


public class Fenetre extends JFrame {

	private static ResourceBundle messages;

	private String selectedLanguage = "eng";

	// Load the ResourceBundle based on the selected language (e.g., "en" for English or "fr" for French)


	private static final long serialVersionUID = 1L;

	public JPanel c;
	//Statement s;

	private JTextArea gt;
	private JPanel ListeTournois;
	private Vector<String> noms_tournois;
	private JList<String> list;
	private JLabel        label;
	private JButton       creerTournoi;
	private JButton       selectTournoi;	
	private JButton       deleteTournoi;
	private JButton       btournois;
	private JButton       bequipes;
	private JButton       btours;
	private JButton       bmatchs;
	private JButton       bresultats;
	private JButton       bparams;
	
	private boolean tournois_trace  = false;
	private boolean details_trace   = false;
	private boolean equipes_trace   = false;
	private boolean tours_trace     = false;
	private boolean match_trace     = false;
	private boolean resultats_trace = false;
	
	private CardLayout fen;
	static String TOURNOIS;
	static String DETAIL;
	static String EQUIPES;
	static String TOURS;
	static String MATCHS ;
	static String RESULTATS;

	private String dateMessage;

	static  String TITLE;
    public Tournoi t = null;
    
    private JLabel statut_select = null;
    private  String statut_deft;

	private String currentDate;


	public Fenetre(){

//		try {
//			UIManager.setLookAndFeel(new FlatDarculaLaf()); // Replace with your desired FlatLaf theme
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
		selectLanguage();


		messages = ResourceBundle.getBundle("messages", new Locale(selectedLanguage));

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale(selectedLanguage));
		currentDate = dateFormat.format(new Date());

		// Replace the DATE placeholder with the current date in the properties file
		String dateMessage = messages.getString("DATE");
		this.dateMessage = dateMessage.replace("@@DATE@@", currentDate);

		TOURNOIS = messages.getString("TOURNOIS");
		DETAIL = messages.getString("DETAIL");
		EQUIPES = messages.getString("EQUIPES");
		TOURS = messages.getString("TOURS");
		MATCHS = messages.getString("MATCHS");
		RESULTATS = messages.getString("RESULTATS");
		TITLE = messages.getString("TITLE");
		statut_deft = messages.getString("STATUS_MESSAGE");


		this.setTitle(TITLE);
		setIcon();
		setSize(800,400);
		this.setVisible(true);
		this.setLocationRelativeTo(this.getParent());
		
		
		JPanel contenu = new JPanel();
		contenu.setLayout(new BorderLayout());
		this.setContentPane(contenu);
		
		
		JPanel phaut = new JPanel();
		contenu.add(phaut,BorderLayout.NORTH);
		
		phaut.add(statut_select = new JLabel());
		this.setStatutSelect(messages.getString("TOURNOI_NON_SELECTIONNE"));
		
		JPanel pgauche = new JPanel();
		pgauche.setPreferredSize(new Dimension(130,0));
		contenu.add(pgauche,BorderLayout.WEST);
		
		
		btournois    = new JButton(TOURNOIS);
		bparams      = new JButton(DETAIL);
		bequipes     = new JButton(EQUIPES);
		btours       = new JButton(TOURS);
		bmatchs      = new JButton(MATCHS);
		bresultats   = new JButton(RESULTATS);
		
		
		int taille_boutons = 100;
		int hauteur_boutons = 30;
		
		btournois.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		bparams.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		bequipes.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		btours.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		bmatchs.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		bresultats.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		
		pgauche.add(btournois);
		pgauche.add(bparams);
		pgauche.add(bequipes);
		pgauche.add(btours);
		pgauche.add(bmatchs);
		pgauche.add(bresultats);
		fen = new CardLayout();
		c = new JPanel(fen);
		
		contenu.add(c,BorderLayout.CENTER);
		
		btournois.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tracer_select_tournoi();	
			}
		});
		btours.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tracer_tours_tournoi();	
			}
		});
		bparams.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tracer_details_tournoi();
			}
		});
		bequipes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tracer_tournoi_equipes();
			}
		});
		bmatchs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tracer_tournoi_matchs();
			}
		});
		bresultats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tracer_tournoi_resultats();
			}
		});
		
		tracer_select_tournoi();
	}
	
	public void setStatutSelect(String t){
		statut_select.setText(statut_deft + "" + t);
	}

	private void setIcon() {
		// Set the icon image
		ImageIcon imgIcon = new ImageIcon(getClass().getResource("/images/logo.png")); // Replace with your icon's path
		this.setIconImage(imgIcon.getImage());
	}
	private void selectLanguage() {
		// Custom icons for the dialog
		ImageIcon languageIcon = new ImageIcon("4363317.png"); // Replace with the path to your icon

		// Custom panel to hold the content
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JLabel label = new JLabel("Select your language");
		label.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Customizing label font and color
		//label.setFont(new Font("Arial", Font.BOLD, 14));
		//label.setForeground(new Color(0, 102, 204)); // Example color

		panel.add(label);
		panel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer

		// Define custom options
		Object[] options = {"English", "Français"};

		// Customizing buttons
		UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.PLAIN, 12));
		UIManager.put("Button.background", new Color(16, 172, 132)); // Example color rgb(16, 172, 132)
		UIManager.put("Button.foreground", Color.WHITE); // Example color rgb(87, 101, 116)

		int choice = JOptionPane.showOptionDialog(null,
				panel,
				"Language Selection",
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				languageIcon,
				options,
				options[0]);

		if (choice == 1) {
			selectedLanguage = "fr";
		} else {
			selectedLanguage = "eng";
		}

		// Reload the ResourceBundle with the selected language
		messages = ResourceBundle.getBundle("messages", new Locale(selectedLanguage));
	}
	public void majboutons(){
		MatchDAO matchDAO = FactoryDAO.getMatchDAO();
		if(t == null){
			btournois.setEnabled(true);
			bequipes.setEnabled(false);
			bmatchs.setEnabled(false);
			btours.setEnabled(false);
			bresultats.setEnabled(false);
			bparams.setEnabled(false);
		}else{
			switch(t.getStatut()){
				case 0:
					btournois.setEnabled(true);
					bequipes.setEnabled(true);
					bmatchs.setEnabled(t.getNbMatchs() > 0);
					btours.setEnabled(t.getNbTours() > 0);
					bresultats.setEnabled(t.getNbMatchs() > 0 && t.areAllMatchesTerminated()); // You need to implement logic to determine if all matches are finished
					bparams.setEnabled(true);
					break;
				case 2:
					btournois.setEnabled(true);
					bequipes.setEnabled(true);
					bmatchs.setEnabled(t.getNbTours() > 0);
					btours.setEnabled(true);
					MatchDAO matchDao = new MatchDAO();


					int total=-1, termines=-1;
					try (PreparedStatement pstmt = matchDao.connection.prepareStatement(
							"SELECT COUNT(*) AS total, " +
									"(SELECT COUNT(*) FROM matchs m2 WHERE m2.id_tournoi = m.id_tournoi AND m2.termine = 'oui') AS termines " +
									"FROM matchs m WHERE m.id_tournoi = ? GROUP BY id_tournoi")) {
						pstmt.setInt(1, this.t.getIdTournoi()); // Set the id_tournoi parameter

						try (ResultSet rs = pstmt.executeQuery()) {
							if (rs.next()) {
								total = rs.getInt("total");
								termines = rs.getInt("termines");
							} else {
								System.out.println(
										"no rows found"
								);
								// Handle the case where no rows are returned
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
						return;
					}

					bresultats.setEnabled(total == termines && total > 0);
					bparams.setEnabled(true);
					break;
			}
		}
	}

	public void tracer_select_tournoi() {
		t = null;
		majboutons();
		setStatutSelect(messages.getString("SELECT_TOURNAMENT"));

		TournoiDAO tournoiDao = FactoryDAO.getTournoiDAO();
		Vector<String> noms_tournois = tournoiDao.getTournoiNames();
		int nbdeLignes = noms_tournois.size();

		if (tournois_trace) {
			list.setListData(noms_tournois);
			selectTournoi.setEnabled(nbdeLignes != 0);
			deleteTournoi.setEnabled(nbdeLignes != 0);
			if (nbdeLignes > 0) {
				list.setSelectedIndex(0);
			}
			fen.show(c, TOURNOIS);
		} else {
			tournois_trace = true;
			JPanel t = new JPanel();
			t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
			c.add(t, TOURNOIS);

			// UI components initialization
			JTextArea gt = new JTextArea(dateMessage);
			gt.setAlignmentX(Component.CENTER_ALIGNMENT);
			gt.setEditable(false);
			t.add(gt);

			JPanel listeTournois = new JPanel();
			t.add(listeTournois);

			list = new JList<>(noms_tournois);
			list.setAlignmentX(Component.LEFT_ALIGNMENT);
			list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			list.setVisibleRowCount(-1);
			JScrollPane listScroller = new JScrollPane(list);
			listScroller.setPreferredSize(new Dimension(250, 180));

			JLabel label = new JLabel(messages.getString("TOURNOI_LIST"));
			label.setLabelFor(list);
			label.setAlignmentX(Component.LEFT_ALIGNMENT);
			t.add(label);
			t.add(listScroller);
			t.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

			Box bh = Box.createHorizontalBox();
			t.add(bh);
			creerTournoi = new JButton(messages.getString("CREER_TOURNOI"));
			selectTournoi = new JButton(messages.getString("SELECT_TOURNAMENT"));
			deleteTournoi = new JButton(messages.getString("SUPPRIMER_TOURNOI"));
			bh.add(creerTournoi);
			bh.add(selectTournoi);
			bh.add(deleteTournoi);

			t.updateUI();
			selectTournoi.setEnabled(nbdeLignes != 0);
			deleteTournoi.setEnabled(nbdeLignes != 0);

			// Event listeners
			creerTournoi.addActionListener(e -> {
				Tournoi.creerTournoi();
				tracer_select_tournoi();
			});

			deleteTournoi.addActionListener(e -> {
				Tournoi.deleteTournoi(list.getSelectedValue());
				tracer_select_tournoi();
			});

			selectTournoi.addActionListener(e -> {
				String nt = list.getSelectedValue();
				this.t = new Tournoi(nt);
				tracer_details_tournoi();
				setStatutSelect(messages.getString("TOURNOI") + nt + "\"");
			});

			fen.show(c, TOURNOIS);
		}
	}
    
	JLabel                     detailt_nom;
	JLabel                     detailt_statut;
	JLabel                     detailt_nbtours;
	//JButton                    detailt_enregistrer;

	public void tracer_details_tournoi() {
		if (t == null) {
			return;
		}
		majboutons();

		// Update existing UI components if they have already been created
		if (details_trace) {
			updateTournoiDetails();
		} else {
			// Initialize UI components for the first time
			initializeTournoiDetailsUI();
		}
		fen.show(c, DETAIL);
	}

	private void updateTournoiDetails() {
		detailt_nom.setText(t.getNomTournoi());
		detailt_statut.setText(Integer.toString(t.getStatut()));
		detailt_nbtours.setText(Integer.toString(t.getNbTours()));
	}

	private void initializeTournoiDetailsUI() {
		details_trace = true;
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.add(new JLabel(messages.getString("DETAIL_TOUR")));
		c.add(p, DETAIL);

		JPanel tab = new JPanel(new GridLayout(4, 2));
		detailt_nom = new JLabel(t.getNomTournoi());
		detailt_statut = new JLabel(Integer.toString(t.getStatut()));
		detailt_nbtours = new JLabel(Integer.toString(t.getNbTours()));

		addDetailRow(tab, messages.getString("NOM_TOURN"), detailt_nom);
		addDetailRow(tab, messages.getString("STATUT"), detailt_statut);
		addDetailRow(tab, messages.getString("NB_TOUR"), detailt_nbtours);

		p.add(tab);
		p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
	}

	private void addDetailRow(JPanel panel, String label, JLabel dataLabel) {
		panel.add(new JLabel(label));
		panel.add(dataLabel);
	}


	private AbstractTableModel eq_modele;
    private JButton            eq_ajouter;
    private JButton            eq_supprimer;
    private JButton            eq_valider;
    private JScrollPane        eq_js;
    JTable                     eq_jt;
    JPanel                     eq_p;
    BoxLayout                  eq_layout;
    JLabel                     eq_desc;

	public void tracer_tournoi_equipes(){
		if(t == null){
			return ;
		}
		majboutons();
		if(equipes_trace){
			t.majEquipes();
			eq_modele.fireTableDataChanged();
		}else{
			equipes_trace = true;
			eq_p      = new JPanel();
			eq_layout = new BoxLayout(eq_p, BoxLayout.Y_AXIS);
			eq_p.setLayout(eq_layout);
			eq_desc = new JLabel(messages.getString("TOURNOI_EQUIPES"));
			eq_p.add(eq_desc);
			eq_p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			c.add(eq_p, EQUIPES);
	
			eq_modele = new AbstractTableModel() {
				

				private static final long serialVersionUID = 1L;

				@Override
				public Object getValueAt(int arg0, int arg1) {
					Object r=null;
					switch(arg1){
					case 0:
						r= t.getEquipe(arg0).getNumber();
					break;
					case 1:
						r= t.getEquipe(arg0).getJoueur1();
					break;
					case 2:
						r= t.getEquipe(arg0).getJoueur2();
					break;
					}
					return r;
		
				}
				public String getColumnName(int col) {
				        if(col == 0){
				        	return messages.getString("N_EQ");
				        }else if(col == 1){
				        	return messages.getString("N_J1");
				        }else if(col == 2){
				        	return messages.getString("N_J2");
				        }else{
				        	return "??";
				        }
				 }
				@Override
				public int getRowCount() {
					if(t == null)return 0;
					return t.getNbEquipes();
				}
				
				@Override
				public int getColumnCount() {
					return 3;
				}
				public boolean isCellEditable(int x, int y){
					if(t.getStatut() != 0) return false;
					return y > 0;
				}
				public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
					Equipe e = t.getEquipe(rowIndex);
					if( columnIndex == 0){
						
					}else if( columnIndex == 1){
						e.setJoueur1((String)aValue);
					}else if( columnIndex == 2){
						e.setJoueur2((String)aValue);
					}
					t.majEquipe(rowIndex);
					fireTableDataChanged();
				}
			};
			eq_jt = new JTable(eq_modele);
			eq_js = new JScrollPane(eq_jt);
			eq_p.add(eq_js);
			//jt.setPreferredSize(getMaximumSize());

			JPanel bt    = new JPanel();
			eq_ajouter   = new JButton(messages.getString("AJ_EQ"));
			eq_supprimer = new JButton(messages.getString("SUPP_EQ"));
			eq_valider   = new JButton(messages.getString("VAL_EQ"));
			
			eq_ajouter.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					t.ajouterEquipe();
					eq_valider.setEnabled(t.getNbEquipes() > 0 && t.getNbEquipes() % 2 == 0) ;
					eq_modele.fireTableDataChanged();
					if(t.getNbEquipes() > 0){
						eq_jt.getSelectionModel().setSelectionInterval(0, 0);
					
					}
					
					
				}
			});
			eq_supprimer.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectedRow = Fenetre.this.eq_jt.getSelectedRow();
					if (selectedRow != -1) {
						int equipeId = Fenetre.this.t.getEquipe(selectedRow).getId();
						Fenetre.this.t.supprimerEquipe(equipeId);
						Fenetre.this.t.majEquipes(); // Refresh the list of teams
						((AbstractTableModel) Fenetre.this.eq_jt.getModel()).fireTableDataChanged();
					}
				}
			});

			eq_valider.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					t.genererMatchs();
					Fenetre.this.majboutons();
					Fenetre.this.tracer_tournoi_matchs();
					
				}
			});
			if(t.getNbEquipes() > 0){
				eq_jt.getSelectionModel().setSelectionInterval(0, 0);
			}
			bt.add(eq_ajouter);
			bt.add(eq_supprimer);
			bt.add(eq_valider);
			eq_p.add(bt);
			eq_p.add(new JLabel(messages.getString("IMPAIR")));
		}
		if(t.getStatut() != 0){
			eq_ajouter.setEnabled(false);
			eq_supprimer.setEnabled(false);
			eq_valider.setEnabled(t.getStatut() == 1);
		}else{
			eq_ajouter.setEnabled(true);
			eq_supprimer.setEnabled(true);	
			eq_valider.setEnabled(t.getNbEquipes() > 0) ;
		}
		fen.show(c, EQUIPES);
		
	}

	JTable                     tours_t;
	JScrollPane                tours_js;
	JPanel                     tours_p;
	BoxLayout                  tours_layout;
	JLabel                     tours_desc;
	
	JButton                    tours_ajouter;
	JButton                    tours_supprimer;
	JButton                    tours_rentrer;

	public void tracer_tours_tournoi(){

		MatchDAO matchDao = FactoryDAO.getMatchDAO();


		if(t == null){
			return ;
		}
		majboutons();
		Vector< Vector<Object>> to =new Vector<Vector<Object>>();
		Vector<Object> v;
		boolean peutajouter = true;
		try (PreparedStatement pstmt = matchDao.connection.prepareStatement(
				"SELECT num_tour, COUNT(*) AS tmatchs, " +
						"(SELECT COUNT(*) FROM matchs m2 WHERE m2.id_tournoi = m.id_tournoi AND m2.num_tour = m.num_tour AND m2.termine = 'oui') AS termines " +
						"FROM matchs m WHERE m.id_tournoi = ? GROUP BY m.num_tour, m.id_tournoi")) {

			pstmt.setInt(1, this.t.getIdTournoi()); // Set the id_tournoi parameter

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					v = new Vector<>();
					v.add(rs.getInt("num_tour"));
					v.add(rs.getInt("tmatchs"));
					v.add(rs.getString("termines"));
					to.add(v);
					peutajouter = peutajouter && rs.getInt("tmatchs") == rs.getInt("termines");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Numéro du tour");
		columnNames.add("Nombre de matchs");
		columnNames.add("Matchs joués");
		tours_t = new JTable(to,columnNames );
		if(tours_trace){
			tours_js.setViewportView(tours_t);
		}else{
			tours_trace  = true;
			tours_p      = new JPanel();
			tours_layout = new BoxLayout( tours_p, BoxLayout.Y_AXIS);
			tours_p.setLayout( tours_layout);
			tours_desc = new JLabel("Tours");
			tours_p.add(tours_desc);
			tours_p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			c.add(tours_p, TOURS);



			tours_js = new JScrollPane();
			tours_js.setViewportView(tours_t);
			tours_p.add(tours_js);

			JPanel bt    = new JPanel();
			tours_ajouter   = new JButton("Ajouter un tour");
			tours_supprimer = new JButton("Supprimer le dernier tour");
			//tours_rentrer   = new JButton("Rentrer les scores du tour s�lectionn�");
			bt.add(tours_ajouter);
			bt.add(tours_supprimer);
			//bt.add(tours_rentrer);
			tours_p.add(bt);
			tours_p.add(new JLabel("Pour pouvoir ajouter un tour, terminez tous les matchs du précédent."));
			tours_p.add(new JLabel("Le nombre maximum de tours est \"le nombre total d'équipes - 1\""));

			tours_ajouter.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (t.ajouterTour()) {
						tracer_tours_tournoi(); // Refresh the panel to show the new tour
					}
				}
			});

			tours_supprimer.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					t.supprimerTour();
					tracer_tours_tournoi(); // Refresh the panel to reflect the tour deletion
				}
			});

		}
		// Logic to enable/disable buttons
		tours_ajouter.setEnabled(t.areAllMatchesTerminated() && t.getNbTours() < t.getNbEquipes() - 1);
		tours_supprimer.setEnabled(t.getNbTours() > 1);

		fen.show(c, TOURS);
		//tours_ajouter.setEnabled(peutajouter);
	}

	private AbstractTableModel match_modele;
    private JScrollPane        match_js;
    JTable                     match_jt;
    JPanel                     match_p;
    BoxLayout                  match_layout;
    JLabel                     match_desc;
    JPanel                     match_bas;
    JLabel                     match_statut;
    JButton                    match_valider;

	public void tracer_tournoi_matchs(){
		if(t == null){
			return ;
		}
		majboutons();
		if(match_trace){
			t.majMatchs();
			match_modele.fireTableDataChanged();
			majStatutM();
		}else{
			match_trace = true;
			match_p      = new JPanel();
			match_layout = new BoxLayout(match_p, BoxLayout.Y_AXIS);
			match_p.setLayout(match_layout);
			match_desc = new JLabel(messages.getString("MATCHS_TOURNOI"));
			match_p.add(match_desc);
			match_p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			c.add(match_p, MATCHS );
	
			match_modele = new AbstractTableModel() {
				private static final long serialVersionUID = 1L;
				@Override
				public Object getValueAt(int arg0, int arg1) {
					Object r=null;
					switch(arg1){
					case 0:
						r= t.getMatch(arg0).getNumTour();
					break;
					case 1:
						r= t.getMatch(arg0).getEq1();
					break;
					case 2:
						r= t.getMatch(arg0).getEq2();
					break;
					case 3:
						r= t.getMatch(arg0).getScore1();
					break;
					case 4:
						r= t.getMatch(arg0).getScore2();
					break;
					}
					return r;
		
				}
				public String getColumnName(int col) {
				        if(col == 0){
				        	return messages.getString("TOURS");
				        }else if(col == 1){
				        	return messages.getString("EQ1");
				        }else if(col == 2){
				        	return messages.getString("EQ2");
				        }else if(col == 3){
				        	return messages.getString("S_EQ1");
				        }else if(col == 4){
				        	return messages.getString("S_EQ2");
				        }else{
				        	return "??";
				        }
				 }
				@Override
				public int getRowCount() {
					// TODO Auto-generated method stub
					if(t == null)return 0;
					return t.getNbMatchs();
				}
				
				@Override
				public int getColumnCount() {
					// TODO Auto-generated method stub
					return 5;
				}
				public boolean isCellEditable(int x, int y){
					return y > 2 && t.getMatch(x).getNumTour() == t.getNbTours();
				}
				public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
					Match m = t.getMatch(rowIndex);
					if (columnIndex == 3 || columnIndex == 4) { // If either score is being updated
						try {
							int score = Integer.parseInt((String) aValue);
							if (columnIndex == 3) {
								m.setScore1(score);
							} else {
								m.setScore2(score);
							}
							// If both scores are non-zero, we can consider the match as finished.
							if (m.getScore1() > 0 || m.getScore2() > 0) {
								m.setTermine(true); // Set the match as finished
							}
							t.majMatch(rowIndex); // Update the match in the database, including the 'termine' flag
							fireTableDataChanged();
							majStatutM(); // Update the status of matches
						} catch (NumberFormatException e) {
							throw new IllegalArgumentException("Number must be > 0");
						}
					}
				}
			};
			match_jt = new JTable(match_modele);

			match_js = new JScrollPane(match_jt);
			match_p.add(match_js);
			//jt.setPreferredSize(getMaximumSize());

			System.out.println("truc2");
			
			match_bas = new JPanel();
			match_bas.add(match_statut = new JLabel(messages.getString("MATCH_JOUEES")));
			match_bas.add(match_valider = new JButton(messages.getString("AFFICHER_RESULTATS")));
			match_valider.setEnabled(false);
			
			match_p.add(match_bas);
			majStatutM();

			
		}

		fen.show(c, MATCHS);
		
	}
	
    private JScrollPane        resultats_js;
    JTable                     resultats_jt;
    JPanel                     resultats_p;
    BoxLayout                  resultats_layout;
    JLabel                     resultats_desc;
    JPanel                     resultats_bas;
    JLabel                     resultats_statut;


	public void tracer_tournoi_resultats(){
		MatchDAO matchDao = FactoryDAO.getMatchDAO();

		Vector<Match> terminatedMatches = new Vector<>(); // Vector to hold terminated matches

		for (Match match : t.datam) { // Assume getAllMatches() fetches all matches for the tournoi
			if (match.isTermine()) { // Add only terminated matches
				terminatedMatches.add(match);
			}
		}

		if(t == null){
			return ;
		}

		Vector< Vector<Object>> to =new Vector<Vector<Object>>();
		Vector<Object> v;
		String sql = "SELECT \n" +
				"    equipe,\n" +
				"    (\n" +
				"        SELECT nom_j1 \n" +
				"        FROM equipes e \n" +
				"        WHERE e.id_equipe = equipe \n" +
				"        AND e.id_tournoi = 123 /* this.t.getIdTournoi() */\n" +
				"    ) AS joueur1,\n" +
				"    (\n" +
				"        SELECT nom_j2 \n" +
				"        FROM equipes e \n" +
				"        WHERE e.id_equipe = equipe \n" +
				"        AND e.id_tournoi = 123 /* this.t.getIdTournoi() */\n" +
				"    ) AS joueur2, \n" +
				"    SUM(score) AS score, \n" +
				"    (\n" +
				"        SELECT COUNT(*) \n" +
				"        FROM matchs m \n" +
				"        WHERE \n" +
				"            (m.equipe1 = equipe AND m.score1 > m.score2 AND m.id_tournoi = id_tournoi) \n" +
				"            OR \n" +
				"            (m.equipe2 = equipe AND m.score2 > m.score1 AND m.id_tournoi = id_tournoi)\n" +
				"    ) AS matchs_gagnes, \n" +
				"    (\n" +
				"        SELECT COUNT(*) \n" +
				"        FROM matchs m \n" +
				"        WHERE \n" +
				"            m.equipe1 = equipe \n" +
				"            OR \n" +
				"            m.equipe2 = equipe\n" +
				"    ) AS matchs_joues \n" +
				"FROM  \n" +
				"    (\n" +
				"        SELECT equipe1 AS equipe, score1 AS score \n" +
				"        FROM matchs \n" +
				"        WHERE id_tournoi = 123 /* this.t.getIdTournoi() */\n" +
				"        UNION \n" +
				"        SELECT equipe2 AS equipe, score2 AS score \n" +
				"        FROM matchs \n" +
				"        WHERE id_tournoi = 123 /* this.t.getIdTournoi() */\n" +
				"    ) \n" +
				"GROUP BY equipe \n" +
				"ORDER BY matchs_gagnes DESC;\n";

		try (PreparedStatement pstmt = matchDao.connection.prepareStatement(sql)) {
			int idTournoi = this.t.getIdTournoi();
			pstmt.setInt(1, idTournoi);
			pstmt.setInt(2, idTournoi);
			pstmt.setInt(3, idTournoi);
			pstmt.setInt(4, idTournoi);
			pstmt.setInt(5, idTournoi);

			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					v = new Vector<>();
					v.add(rs.getInt("equipe"));
					v.add(rs.getString("joueur1"));
					v.add(rs.getString("joueur2"));
					v.add(rs.getInt("score"));
					v.add(rs.getInt("matchs_gagnes"));
					v.add(rs.getInt("matchs_joues"));
					to.add(v);
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Numéro d'équipe");
		columnNames.add("Nom joueur 1");
		columnNames.add("Nom joueur 2");
		columnNames.add("Score");
		columnNames.add("Matchs gagnés");
		columnNames.add("Matchs joués");
		resultats_jt = new JTable(to,columnNames );
		resultats_jt.setAutoCreateRowSorter(true);

		if(resultats_trace){
			resultats_js.setViewportView(resultats_jt);
		}else{
			resultats_trace = true;
			resultats_p      = new JPanel();
			resultats_layout = new BoxLayout(resultats_p, BoxLayout.Y_AXIS);

			resultats_p.setLayout(resultats_layout);
			resultats_desc = new JLabel("Résultats du tournoi");
			resultats_p.add(resultats_desc);
			resultats_p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			c.add(resultats_p, RESULTATS );





			resultats_js = new JScrollPane(resultats_jt);
			resultats_p.add(resultats_js);
			//jt.setPreferredSize(getMaximumSize());


			resultats_bas = new JPanel();
			resultats_bas.add(resultats_statut = new JLabel("Gagnant:"));

			resultats_p.add(resultats_bas);
		}

		fen.show(c, RESULTATS);

	}

	private void majStatutM() {
		MatchDAO matchDao = FactoryDAO.getMatchDAO();
		int[] status = matchDao.getMatchStatus(this.t.getIdTournoi());

		match_statut.setText(status[1] + "/" + status[0] + messages.getString("MATCH_JOUEES"));
		match_valider.setEnabled(status[0] == status[1]);
	}
}
