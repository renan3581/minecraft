package me.seuplugin.troca;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SistemaDeTroca extends JavaPlugin {

    // Mapa para armazenar as trocas em andamento. Chave: UUID do jogador 1, Valor: Objeto TradeManager
    private Map<UUID, TradeManager> activeTrades;
    // Mapa para armazenar as solicitaÃ§Ãµes de troca pendentes. Chave: UUID do solicitante, Valor: UUID do alvo
    private Map<UUID, UUID> pendingRequests;

    @Override
    public void onEnable() {
        // Inicializa os mapas
        activeTrades = new HashMap<>();
        pendingRequests = new HashMap<>();

        // Registra o listener de eventos
        getServer().getPluginManager().registerEvents(new TradeListener(this), this);

        // Salva a configuraÃ§Ã£o padrÃ£o se ela nÃ£o existir
        saveDefaultConfig();
        // Carrega as mensagens do arquivo de configuraÃ§Ã£o
        Messages.loadMessages(getConfig());

        getLogger().info("Sistema de Troca ativado!");
    }

    @Override
    public void onDisable() {
        // Garante que todas as interfaces de troca abertas sejam fechadas ao desativar o plugin
        for (TradeManager trade : activeTrades.values()) {
            trade.cancelTrade(false); // NÃ£o envia mensagem de cancelamento ao desativar
        }
        activeTrades.clear();
        pendingRequests.clear();
        getLogger().info("Sistema de Troca desativado!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Verifica se o comando Ã© 'trocar'
        if (command.getName().equalsIgnoreCase("trocar")) {
            // Verifica se o sender Ã© um jogador
            if (!(sender instanceof Player)) {
                sender.sendMessage(Messages.ONLY_PLAYERS_COMMAND);
                return true;
            }

            Player player = (Player) sender;

            // Verifica permissÃ£o
            if (!player.hasPermission("troca.comando.usar")) {
                player.sendMessage(Messages.NO_PERMISSION);
                return true;
            }

            // Verifica o nÃºmero de argumentos
            if (args.length != 1) {
                player.sendMessage(Messages.TRADE_COMMAND_USAGE);
                return true;
            }

            // ObtÃ©m o nome do jogador alvo
            String targetName = args[0];
            Player target = Bukkit.getPlayer(targetName);

            // Verifica se o jogador alvo estÃ¡ online
            if (target == null || !target.isOnline()) {
                player.sendMessage(Messages.PLAYER_NOT_FOUND.replace("%player%", targetName));
                return true;
            }

            // NÃ£o pode trocar consigo mesmo
            if (player.equals(target)) {
                player.sendMessage(Messages.CANT_TRADE_SELF);
                return true;
            }

            // Verifica se o jogador jÃ¡ tem uma troca ativa
            if (getActiveTrade(player) != null) {
                player.sendMessage(Messages.ALREADY_IN_TRADE);
                return true;
            }

            // Verifica se o jogador alvo jÃ¡ tem uma troca ativa
            if (getActiveTrade(target) != null) {
                player.sendMessage(Messages.TARGET_ALREADY_IN_TRADE.replace("%player%", target.getName()));
                return true;
            }

            // Verifica se jÃ¡ existe uma solicitaÃ§Ã£o pendente para o jogador alvo
            if (pendingRequests.containsKey(target.getUniqueId()) && pendingRequests.get(target.getUniqueId()).equals(player.getUniqueId())) {
                // Se o alvo jÃ¡ enviou uma solicitaÃ§Ã£o para este jogador, aceita a troca
                startTrade(target, player);
                pendingRequests.remove(target.getUniqueId()); // Remove a solicitaÃ§Ã£o pendente
                return true;
            }

            // Adiciona a solicitaÃ§Ã£o pendente
            pendingRequests.put(player.getUniqueId(), target.getUniqueId());

            // Envia a mensagem de solicitaÃ§Ã£o para o alvo
            target.sendMessage(Messages.TRADE_REQUEST_RECEIVED
                    .replace("%player%", player.getName())
                    .replace("%command%", "/trocar " + player.getName()));
            player.sendMessage(Messages.TRADE_REQUEST_SENT.replace("%player%", target.getName()));

            // Limpa a solicitaÃ§Ã£o apÃ³s um tempo para evitar solicitaÃ§Ãµes eternas
            Bukkit.getScheduler().runTaskLater(this, () -> {
                if (pendingRequests.containsKey(player.getUniqueId()) && pendingRequests.get(player.getUniqueId()).equals(target.getUniqueId())) {
                    pendingRequests.remove(player.getUniqueId());
                    player.sendMessage(Messages.TRADE_REQUEST_EXPIRED_SENDER.replace("%player%", target.getName()));
                    if (target.isOnline()) {
                        target.sendMessage(Messages.TRADE_REQUEST_EXPIRED_TARGET.replace("%player%", player.getName()));
                    }
                }
            }, 20L * 30); // 30 segundos

            return true;
        }
        return false;
    }

    /**
     * Inicia uma nova troca entre dois jogadores.
     * @param player1 O jogador que iniciou ou aceitou a troca.
     * @param player2 O jogador alvo da troca.
     */
    public void startTrade(Player player1, Player player2) {
        // Remove quaisquer solicitaÃ§Ãµes pendentes que possam ter levado a esta troca
        pendingRequests.remove(player1.getUniqueId());
        pendingRequests.remove(player2.getUniqueId());

        TradeManager trade = new TradeManager(this, player1, player2);
        activeTrades.put(player1.getUniqueId(), trade);
        activeTrades.put(player2.getUniqueId(), trade);

        player1.openInventory(trade.getInventory1());
        player2.openInventory(trade.getInventory2());

        player1.sendMessage(Messages.TRADE_STARTED.replace("%player%", player2.getName()));
        player2.sendMessage(Messages.TRADE_STARTED.replace("%player%", player1.getName()));
    }

    /**
     * Remove uma troca ativa do mapa.
     * @param player O jogador cujo TradeManager deve ser removido.
     */
    public void removeActiveTrade(Player player) {
        activeTrades.remove(player.getUniqueId());
    }

    /**
     * ObtÃ©m o TradeManager de um jogador, se ele estiver em uma troca ativa.
     * @param player O jogador a ser verificado.
     * @return O TradeManager se o jogador estiver em uma troca, null caso contrÃ¡rio.
     */
    public TradeManager getActiveTrade(Player player) {
        return activeTrades.get(player.getUniqueId());
    }

    /**
     * Verifica se um jogador tem uma solicitaÃ§Ã£o de troca pendente.
     * @param player O jogador a ser verificado.
     * @return true se tiver uma solicitaÃ§Ã£o pendente, false caso contrÃ¡rio.
     */
    public boolean hasPendingRequest(Player player) {
        return pendingRequests.containsKey(player.getUniqueId());
    }

    /**
     * ObtÃ©m o alvo de uma solicitaÃ§Ã£o pendente.
     * @param player O jogador que enviou a solicitaÃ§Ã£o.
     * @return O UUID do alvo, ou null se nÃ£o houver solicitaÃ§Ã£o pendente.
     */
    public UUID getPendingRequestTarget(Player player) {
        return pendingRequests.get(player.getUniqueId());
    }
}