package Views;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;
import java.util.Vector;

import DAOs.FactoryDAO;
import DAOs.TournoiDAO;
import Strategy.*;

public class TournamentSelectionPanel extends JPanel {
    private final MainFrame frame;
    private final ResourceBundle messages;
    private JList<String> tournamentList;
    private JButton selectButton;
    private JButton deleteButton;

    public TournamentSelectionPanel(MainFrame frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        initializeUI();
        refreshTournamentList();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Tournament List Panel
        JPanel listPanel = createTournamentListPanel();
        add(listPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel(getLocalizedString("TOURNOI_LIST"));
        header.setFont(header.getFont().deriveFont(Font.BOLD, 16));
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        headerPanel.add(header, BorderLayout.CENTER);
        return headerPanel;
    }

    private JPanel createTournamentListPanel() {
        JPanel listPanel = new JPanel(new BorderLayout());
        
        tournamentList = new JList<>();
        tournamentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tournamentList.addListSelectionListener(e -> updateButtonStates());
        
        JScrollPane scrollPane = new JScrollPane(tournamentList);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        
        listPanel.add(scrollPane, BorderLayout.CENTER);
        return listPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton createButton = new JButton(getLocalizedString("CREER_TOURNOI"));
        selectButton = new JButton(getLocalizedString("SELECT_TOURNAMENT"));
        deleteButton = new JButton(getLocalizedString("SUPPRIMER_TOURNOI"));

        // Style buttons
        styleButton(createButton);
        styleButton(selectButton);
        styleButton(deleteButton);

        // Add actions
        createButton.addActionListener(e -> handleCreateTournament());
        selectButton.addActionListener(e -> handleSelectTournament());
        deleteButton.addActionListener(e -> handleDeleteTournament());

        // Initial button states
        selectButton.setEnabled(false);
        deleteButton.setEnabled(false);

        buttonPanel.add(createButton);
        buttonPanel.add(selectButton);
        buttonPanel.add(deleteButton);

        return buttonPanel;
    }

    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(150, 30));
        button.setFocusPainted(false);
    }

    private void handleCreateTournament() {
        String tournamentName = showTournamentNameDialog();
        
        if (tournamentName != null && !tournamentName.trim().isEmpty()) {
            Boolean ok = new CreateTournamentStrategy(tournamentName).execute();
            if(ok) {
                refreshTournamentList();
                showSuccessMessage(getLocalizedString("TOURNAMENT_DELETED_SUCCESS"));

            } else  {
                showErrorMessage(getLocalizedString("TOURNAMENT_CREATION_ERROR"));
            }
        }
    }

    private String showTournamentNameDialog() {
        String s = (String)JOptionPane.showInputDialog(
            null,
            "Entrez le nom du tournoi",
            "Nom du tournoi",
            JOptionPane.PLAIN_MESSAGE); 
        return s ; 
    }
    

    private void handleSelectTournament() {
        String selectedTournament = tournamentList.getSelectedValue();
        if (selectedTournament != null) {
            Boolean ok =  new SelectTournamentStrategy(frame, selectedTournament).execute();
            if (!ok){
                this.showErrorMessage("Tournament not found!");
            }

        }
    }

    private void handleDeleteTournament() {
        String selectedTournament = tournamentList.getSelectedValue();
        if (selectedTournament != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                getLocalizedString("CONFIRM_DELETE_MESSAGE"),
                getLocalizedString("CONFIRM_DELETE_TITLE"),
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                Boolean ok =  new DeleteTournamentStrategy(selectedTournament).execute();
                if(ok){
                    refreshTournamentList();
                    showSuccessMessage(getLocalizedString("TOURNAMENT_DELETED_SUCCESS"));
                }else  {
                    showErrorMessage(getLocalizedString("TOURNAMENT_DELETION_ERROR"));
                }
            }
        }
    }

    private void refreshTournamentList() {
        try {
            TournoiDAO dao = FactoryDAO.getTournoiDAO();
            Vector<String> names = dao.getTournoiNames();
            tournamentList.setListData(names);
            updateButtonStates();
        } catch (Exception ex) {
            showErrorMessage(getLocalizedString("TOURNAMENT_LOAD_ERROR"));
        }
    }

    private void updateButtonStates() {
        boolean hasSelection = tournamentList.getSelectedValue() != null;
        selectButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
    }



    private String getLocalizedString(String key) {
        try {
            return messages.getString(key);
        } catch (Exception e) {
            return key;
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            getLocalizedString("ERROR_TITLE"),
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(
            this,
            message,
            getLocalizedString("SUCCESS_TITLE"),
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}