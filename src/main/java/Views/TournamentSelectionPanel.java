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

    public TournamentSelectionPanel(MainFrame frame, ResourceBundle messages) {
        this.frame = frame;
        this.messages = messages;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());

        JLabel header = new JLabel(messages.getString("TOURNOI_LIST"), JLabel.CENTER);
        add(header, BorderLayout.NORTH);

        // Tournament list
        JList<String> tournamentList = new JList<>(getTournamentNames());
        JScrollPane scrollPane = new JScrollPane(tournamentList);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton(messages.getString("CREER_TOURNOI"));
        JButton selectButton = new JButton(messages.getString("SELECT_TOURNAMENT"));
        JButton deleteButton = new JButton(messages.getString("SUPPRIMER_TOURNOI"));

        buttonPanel.add(createButton);
        buttonPanel.add(selectButton);
        buttonPanel.add(deleteButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        createButton.addActionListener(e -> new CreateTournamentStrategy(messages.getString("NOM_TOURN")).execute());
        selectButton.addActionListener(e -> new SelectTournamentStrategy(frame, tournamentList.getSelectedValue()).execute());
        deleteButton.addActionListener(e -> new DeleteTournamentStrategy(tournamentList.getSelectedValue()).execute());
    }

    private String[] getTournamentNames() {
        TournoiDAO dao = FactoryDAO.getTournoiDAO();
        Vector<String> names = dao.getTournoiNames();
        return names.toArray(new String[0]);
    }
}
