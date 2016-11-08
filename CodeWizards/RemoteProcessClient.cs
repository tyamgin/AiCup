using System;
using System.IO;
using System.Net.Sockets;
using System.Text;
using Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk.Model;

namespace Com.CodeGame.CodeWizards2016.DevKit.CSharpCgdk {
    public sealed class RemoteProcessClient {
        private const int BufferSizeBytes = 1 << 20;

        private static readonly byte[] EmptyByteArray = { };

        private readonly TcpClient client;
        private readonly BinaryReader reader;
        private readonly BinaryWriter writer;

        private Tree[] previousTrees;

        public RemoteProcessClient(string host, int port) {
            client = new TcpClient(host, port) {
                SendBufferSize = BufferSizeBytes, ReceiveBufferSize = BufferSizeBytes, NoDelay = true
            };

            reader = new BinaryReader(client.GetStream());
            writer = new BinaryWriter(client.GetStream());
        }

        public void WriteTokenMessage(string token) {
            WriteEnum((sbyte?) MessageType.AuthenticationToken);
            WriteString(token);
            writer.Flush();
        }

        public void WriteProtocolVersionMessage() {
            WriteEnum((sbyte?) MessageType.ProtocolVersion);
            WriteInt(1);
            writer.Flush();
        }

        public int ReadTeamSizeMessage() {
            EnsureMessageType((MessageType) ReadEnum(), MessageType.TeamSize);
            return ReadInt();
        }

        public Game ReadGameContextMessage() {
            EnsureMessageType((MessageType) ReadEnum(), MessageType.GameContext);
            return ReadGame();
        }

        public PlayerContext ReadPlayerContextMessage() {
            MessageType messageType = (MessageType) ReadEnum();
            if (messageType == MessageType.GameOver) {
                return null;
            }

            EnsureMessageType(messageType, MessageType.PlayerContext);
            return ReadPlayerContext();
        }

        public void WriteMovesMessage(Move[] moves) {
            WriteEnum((sbyte?) MessageType.Moves);
            WriteMoves(moves);
            writer.Flush();
        }

        public void Close() {
            client.Close();
        }

        private Bonus ReadBonus() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Bonus(
                    ReadLong(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    (Faction) ReadEnum(), ReadDouble(), (BonusType) ReadEnum()
            );
        }

        private void WriteBonus(Bonus bonus) {
            if (bonus == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(bonus.Id);
            WriteDouble(bonus.X);
            WriteDouble(bonus.Y);
            WriteDouble(bonus.SpeedX);
            WriteDouble(bonus.SpeedY);
            WriteDouble(bonus.Angle);
            WriteEnum((sbyte?) bonus.Faction);
            WriteDouble(bonus.Radius);
            WriteEnum((sbyte?) bonus.Type);
        }

        private Bonus[] ReadBonuses() {
            int bonusCount = ReadInt();
            if (bonusCount < 0) {
                return null;
            }

            Bonus[] bonuses = new Bonus[bonusCount];

            for (int bonusIndex = 0; bonusIndex < bonusCount; ++bonusIndex) {
                bonuses[bonusIndex] = ReadBonus();
            }

            return bonuses;
        }

        private void WriteBonuses(Bonus[] bonuses) {
            if (bonuses == null) {
                WriteInt(-1);
                return;
            }

            int bonusCount = bonuses.Length;
            WriteInt(bonusCount);

            for (int bonusIndex = 0; bonusIndex < bonusCount; ++bonusIndex) {
                WriteBonus(bonuses[bonusIndex]);
            }
        }

        private Building ReadBuilding() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Building(
                    ReadLong(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    (Faction) ReadEnum(), ReadDouble(), ReadInt(), ReadInt(), ReadStatuses(), (BuildingType) ReadEnum(),
                    ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadInt()
            );
        }

        private void WriteBuilding(Building building) {
            if (building == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(building.Id);
            WriteDouble(building.X);
            WriteDouble(building.Y);
            WriteDouble(building.SpeedX);
            WriteDouble(building.SpeedY);
            WriteDouble(building.Angle);
            WriteEnum((sbyte?) building.Faction);
            WriteDouble(building.Radius);
            WriteInt(building.Life);
            WriteInt(building.MaxLife);
            WriteStatuses(building.Statuses);
            WriteEnum((sbyte?) building.Type);
            WriteDouble(building.VisionRange);
            WriteDouble(building.AttackRange);
            WriteInt(building.Damage);
            WriteInt(building.CooldownTicks);
            WriteInt(building.RemainingActionCooldownTicks);
        }

        private Building[] ReadBuildings() {
            int buildingCount = ReadInt();
            if (buildingCount < 0) {
                return null;
            }

            Building[] buildings = new Building[buildingCount];

            for (int buildingIndex = 0; buildingIndex < buildingCount; ++buildingIndex) {
                buildings[buildingIndex] = ReadBuilding();
            }

            return buildings;
        }

        private void WriteBuildings(Building[] buildings) {
            if (buildings == null) {
                WriteInt(-1);
                return;
            }

            int buildingCount = buildings.Length;
            WriteInt(buildingCount);

            for (int buildingIndex = 0; buildingIndex < buildingCount; ++buildingIndex) {
                WriteBuilding(buildings[buildingIndex]);
            }
        }

        private Game ReadGame() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Game(
                    ReadLong(), ReadInt(), ReadDouble(), ReadBoolean(), ReadBoolean(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(),
                    ReadDouble(), ReadInt(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(),
                    ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(),
                    ReadInt(), ReadDouble(), ReadDouble(), ReadInts(), ReadDouble(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadDouble(), ReadDouble(), ReadInt(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadDouble(), ReadDouble(),
                    ReadInt(), ReadDouble(), ReadDouble(), ReadInt(), ReadDouble(), ReadDouble(), ReadInt(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadDouble(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadInt(), ReadDouble(),
                    ReadInt(), ReadInt(), ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(), ReadDouble(),
                    ReadDouble(), ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadDouble(), ReadInt()
            );
        }

        private void WriteGame(Game game) {
            if (game == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(game.RandomSeed);
            WriteInt(game.TickCount);
            WriteDouble(game.MapSize);
            WriteBoolean(game.IsSkillsEnabled);
            WriteBoolean(game.IsRawMessagesEnabled);
            WriteDouble(game.FriendlyFireDamageFactor);
            WriteDouble(game.BuildingDamageScoreFactor);
            WriteDouble(game.BuildingEliminationScoreFactor);
            WriteDouble(game.MinionDamageScoreFactor);
            WriteDouble(game.MinionEliminationScoreFactor);
            WriteDouble(game.WizardDamageScoreFactor);
            WriteDouble(game.WizardEliminationScoreFactor);
            WriteDouble(game.TeamWorkingScoreFactor);
            WriteInt(game.VictoryScore);
            WriteDouble(game.ScoreGainRange);
            WriteInt(game.RawMessageMaxLength);
            WriteDouble(game.RawMessageTransmissionSpeed);
            WriteDouble(game.WizardRadius);
            WriteDouble(game.WizardCastRange);
            WriteDouble(game.WizardVisionRange);
            WriteDouble(game.WizardForwardSpeed);
            WriteDouble(game.WizardBackwardSpeed);
            WriteDouble(game.WizardStrafeSpeed);
            WriteInt(game.WizardBaseLife);
            WriteInt(game.WizardLifeGrowthPerLevel);
            WriteInt(game.WizardBaseMana);
            WriteInt(game.WizardManaGrowthPerLevel);
            WriteDouble(game.WizardBaseLifeRegeneration);
            WriteDouble(game.WizardLifeRegenerationGrowthPerLevel);
            WriteDouble(game.WizardBaseManaRegeneration);
            WriteDouble(game.WizardManaRegenerationGrowthPerLevel);
            WriteDouble(game.WizardMaxTurnAngle);
            WriteInt(game.WizardMaxResurrectionDelayTicks);
            WriteInt(game.WizardMinResurrectionDelayTicks);
            WriteInt(game.WizardActionCooldownTicks);
            WriteInt(game.StaffCooldownTicks);
            WriteInt(game.MagicMissileCooldownTicks);
            WriteInt(game.FrostBoltCooldownTicks);
            WriteInt(game.FireballCooldownTicks);
            WriteInt(game.HasteCooldownTicks);
            WriteInt(game.ShieldCooldownTicks);
            WriteInt(game.MagicMissileManacost);
            WriteInt(game.FrostBoltManacost);
            WriteInt(game.FireballManacost);
            WriteInt(game.HasteManacost);
            WriteInt(game.ShieldManacost);
            WriteInt(game.StaffDamage);
            WriteDouble(game.StaffSector);
            WriteDouble(game.StaffRange);
            WriteInts(game.LevelUpXpValues);
            WriteDouble(game.MinionRadius);
            WriteDouble(game.MinionVisionRange);
            WriteDouble(game.MinionSpeed);
            WriteDouble(game.MinionMaxTurnAngle);
            WriteInt(game.MinionLife);
            WriteInt(game.FactionMinionAppearanceIntervalTicks);
            WriteInt(game.OrcWoodcutterActionCooldownTicks);
            WriteInt(game.OrcWoodcutterDamage);
            WriteDouble(game.OrcWoodcutterAttackSector);
            WriteDouble(game.OrcWoodcutterAttackRange);
            WriteInt(game.FetishBlowdartActionCooldownTicks);
            WriteDouble(game.FetishBlowdartAttackRange);
            WriteDouble(game.FetishBlowdartAttackSector);
            WriteDouble(game.BonusRadius);
            WriteInt(game.BonusAppearanceIntervalTicks);
            WriteInt(game.BonusScoreAmount);
            WriteDouble(game.DartRadius);
            WriteDouble(game.DartSpeed);
            WriteInt(game.DartDirectDamage);
            WriteDouble(game.MagicMissileRadius);
            WriteDouble(game.MagicMissileSpeed);
            WriteInt(game.MagicMissileDirectDamage);
            WriteDouble(game.FrostBoltRadius);
            WriteDouble(game.FrostBoltSpeed);
            WriteInt(game.FrostBoltDirectDamage);
            WriteDouble(game.FireballRadius);
            WriteDouble(game.FireballSpeed);
            WriteDouble(game.FireballExplosionMaxDamageRange);
            WriteDouble(game.FireballExplosionMinDamageRange);
            WriteInt(game.FireballExplosionMaxDamage);
            WriteInt(game.FireballExplosionMinDamage);
            WriteDouble(game.GuardianTowerRadius);
            WriteDouble(game.GuardianTowerVisionRange);
            WriteDouble(game.GuardianTowerLife);
            WriteDouble(game.GuardianTowerAttackRange);
            WriteInt(game.GuardianTowerDamage);
            WriteInt(game.GuardianTowerCooldownTicks);
            WriteDouble(game.FactionBaseRadius);
            WriteDouble(game.FactionBaseVisionRange);
            WriteDouble(game.FactionBaseLife);
            WriteDouble(game.FactionBaseAttackRange);
            WriteInt(game.FactionBaseDamage);
            WriteInt(game.FactionBaseCooldownTicks);
            WriteInt(game.BurningDurationTicks);
            WriteInt(game.BurningSummaryDamage);
            WriteInt(game.EmpoweredDurationTicks);
            WriteDouble(game.EmpoweredDamageFactor);
            WriteInt(game.FrozenDurationTicks);
            WriteInt(game.HastenedDurationTicks);
            WriteDouble(game.HastenedBonusDurationFactor);
            WriteDouble(game.HastenedMovementBonusFactor);
            WriteDouble(game.HastenedRotationBonusFactor);
            WriteInt(game.ShieldedDurationTicks);
            WriteDouble(game.ShieldedBonusDurationFactor);
            WriteDouble(game.ShieldedDirectDamageAbsorptionFactor);
            WriteDouble(game.AuraSkillRange);
            WriteDouble(game.RangeBonusPerSkillLevel);
            WriteInt(game.MagicalDamageBonusPerSkillLevel);
            WriteInt(game.StaffDamageBonusPerSkillLevel);
            WriteDouble(game.MovementBonusFactorPerSkillLevel);
            WriteInt(game.MagicalDamageAbsorptionPerSkillLevel);
        }

        private Game[] ReadGames() {
            int gameCount = ReadInt();
            if (gameCount < 0) {
                return null;
            }

            Game[] games = new Game[gameCount];

            for (int gameIndex = 0; gameIndex < gameCount; ++gameIndex) {
                games[gameIndex] = ReadGame();
            }

            return games;
        }

        private void WriteGames(Game[] games) {
            if (games == null) {
                WriteInt(-1);
                return;
            }

            int gameCount = games.Length;
            WriteInt(gameCount);

            for (int gameIndex = 0; gameIndex < gameCount; ++gameIndex) {
                WriteGame(games[gameIndex]);
            }
        }

        private Message ReadMessage() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Message((LaneType) ReadEnum(), (SkillType?) ReadEnum(), ReadByteArray(false));
        }

        private void WriteMessage(Message message) {
            if (message == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteEnum((sbyte?) message.Lane);
            WriteEnum((sbyte?) message.SkillToLearn);
            WriteByteArray(message.RawMessage);
        }

        private Message[] ReadMessages() {
            int messageCount = ReadInt();
            if (messageCount < 0) {
                return null;
            }

            Message[] messages = new Message[messageCount];

            for (int messageIndex = 0; messageIndex < messageCount; ++messageIndex) {
                messages[messageIndex] = ReadMessage();
            }

            return messages;
        }

        private void WriteMessages(Message[] messages) {
            if (messages == null) {
                WriteInt(-1);
                return;
            }

            int messageCount = messages.Length;
            WriteInt(messageCount);

            for (int messageIndex = 0; messageIndex < messageCount; ++messageIndex) {
                WriteMessage(messages[messageIndex]);
            }
        }

        private Minion ReadMinion() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Minion(
                    ReadLong(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    (Faction) ReadEnum(), ReadDouble(), ReadInt(), ReadInt(), ReadStatuses(), (MinionType) ReadEnum(),
                    ReadDouble(), ReadInt(), ReadInt(), ReadInt()
            );
        }

        private void WriteMinion(Minion minion) {
            if (minion == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(minion.Id);
            WriteDouble(minion.X);
            WriteDouble(minion.Y);
            WriteDouble(minion.SpeedX);
            WriteDouble(minion.SpeedY);
            WriteDouble(minion.Angle);
            WriteEnum((sbyte?) minion.Faction);
            WriteDouble(minion.Radius);
            WriteInt(minion.Life);
            WriteInt(minion.MaxLife);
            WriteStatuses(minion.Statuses);
            WriteEnum((sbyte?) minion.Type);
            WriteDouble(minion.VisionRange);
            WriteInt(minion.Damage);
            WriteInt(minion.CooldownTicks);
            WriteInt(minion.RemainingActionCooldownTicks);
        }

        private Minion[] ReadMinions() {
            int minionCount = ReadInt();
            if (minionCount < 0) {
                return null;
            }

            Minion[] minions = new Minion[minionCount];

            for (int minionIndex = 0; minionIndex < minionCount; ++minionIndex) {
                minions[minionIndex] = ReadMinion();
            }

            return minions;
        }

        private void WriteMinions(Minion[] minions) {
            if (minions == null) {
                WriteInt(-1);
                return;
            }

            int minionCount = minions.Length;
            WriteInt(minionCount);

            for (int minionIndex = 0; minionIndex < minionCount; ++minionIndex) {
                WriteMinion(minions[minionIndex]);
            }
        }

        private void WriteMove(Move move) {
            if (move == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteDouble(move.Speed);
            WriteDouble(move.StrafeSpeed);
            WriteDouble(move.Turn);
            WriteEnum((sbyte?) move.Action);
            WriteDouble(move.CastAngle);
            WriteDouble(move.MinCastDistance);
            WriteDouble(move.MaxCastDistance);
            WriteLong(move.StatusTargetId);
            WriteEnum((sbyte?) move.SkillToLearn);
            WriteMessages(move.Messages);
        }

        private void WriteMoves(Move[] moves) {
            if (moves == null) {
                WriteInt(-1);
                return;
            }

            int moveCount = moves.Length;
            WriteInt(moveCount);

            for (int moveIndex = 0; moveIndex < moveCount; ++moveIndex) {
                WriteMove(moves[moveIndex]);
            }
        }

        private Player ReadPlayer() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Player(ReadLong(), ReadBoolean(), ReadString(), ReadBoolean(), ReadInt(), (Faction) ReadEnum());
        }

        private void WritePlayer(Player player) {
            if (player == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(player.Id);
            WriteBoolean(player.IsMe);
            WriteString(player.Name);
            WriteBoolean(player.IsStrategyCrashed);
            WriteInt(player.Score);
            WriteEnum((sbyte?) player.Faction);
        }

        private Player[] ReadPlayers() {
            int playerCount = ReadInt();
            if (playerCount < 0) {
                return null;
            }

            Player[] players = new Player[playerCount];

            for (int playerIndex = 0; playerIndex < playerCount; ++playerIndex) {
                players[playerIndex] = ReadPlayer();
            }

            return players;
        }

        private void WritePlayers(Player[] players) {
            if (players == null) {
                WriteInt(-1);
                return;
            }

            int playerCount = players.Length;
            WriteInt(playerCount);

            for (int playerIndex = 0; playerIndex < playerCount; ++playerIndex) {
                WritePlayer(players[playerIndex]);
            }
        }

        private PlayerContext ReadPlayerContext() {
            if (!ReadBoolean()) {
                return null;
            }

            return new PlayerContext(ReadWizards(), ReadWorld());
        }

        private void WritePlayerContext(PlayerContext playerContext) {
            if (playerContext == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteWizards(playerContext.Wizards);
            WriteWorld(playerContext.World);
        }

        private PlayerContext[] ReadPlayerContexts() {
            int playerContextCount = ReadInt();
            if (playerContextCount < 0) {
                return null;
            }

            PlayerContext[] playerContexts = new PlayerContext[playerContextCount];

            for (int playerContextIndex = 0; playerContextIndex < playerContextCount; ++playerContextIndex) {
                playerContexts[playerContextIndex] = ReadPlayerContext();
            }

            return playerContexts;
        }

        private void WritePlayerContexts(PlayerContext[] playerContexts) {
            if (playerContexts == null) {
                WriteInt(-1);
                return;
            }

            int playerContextCount = playerContexts.Length;
            WriteInt(playerContextCount);

            for (int playerContextIndex = 0; playerContextIndex < playerContextCount; ++playerContextIndex) {
                WritePlayerContext(playerContexts[playerContextIndex]);
            }
        }

        private Projectile ReadProjectile() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Projectile(
                    ReadLong(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    (Faction) ReadEnum(), ReadDouble(), (ProjectileType) ReadEnum(), ReadLong(), ReadLong()
            );
        }

        private void WriteProjectile(Projectile projectile) {
            if (projectile == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(projectile.Id);
            WriteDouble(projectile.X);
            WriteDouble(projectile.Y);
            WriteDouble(projectile.SpeedX);
            WriteDouble(projectile.SpeedY);
            WriteDouble(projectile.Angle);
            WriteEnum((sbyte?) projectile.Faction);
            WriteDouble(projectile.Radius);
            WriteEnum((sbyte?) projectile.Type);
            WriteLong(projectile.OwnerUnitId);
            WriteLong(projectile.OwnerPlayerId);
        }

        private Projectile[] ReadProjectiles() {
            int projectileCount = ReadInt();
            if (projectileCount < 0) {
                return null;
            }

            Projectile[] projectiles = new Projectile[projectileCount];

            for (int projectileIndex = 0; projectileIndex < projectileCount; ++projectileIndex) {
                projectiles[projectileIndex] = ReadProjectile();
            }

            return projectiles;
        }

        private void WriteProjectiles(Projectile[] projectiles) {
            if (projectiles == null) {
                WriteInt(-1);
                return;
            }

            int projectileCount = projectiles.Length;
            WriteInt(projectileCount);

            for (int projectileIndex = 0; projectileIndex < projectileCount; ++projectileIndex) {
                WriteProjectile(projectiles[projectileIndex]);
            }
        }

        private Status ReadStatus() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Status(ReadLong(), (StatusType) ReadEnum(), ReadLong(), ReadLong(), ReadInt());
        }

        private void WriteStatus(Status status) {
            if (status == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(status.Id);
            WriteEnum((sbyte?) status.Type);
            WriteLong(status.WizardId);
            WriteLong(status.PlayerId);
            WriteInt(status.RemainingDurationTicks);
        }

        private Status[] ReadStatuses() {
            int statusCount = ReadInt();
            if (statusCount < 0) {
                return null;
            }

            Status[] statuses = new Status[statusCount];

            for (int statusIndex = 0; statusIndex < statusCount; ++statusIndex) {
                statuses[statusIndex] = ReadStatus();
            }

            return statuses;
        }

        private void WriteStatuses(Status[] statuses) {
            if (statuses == null) {
                WriteInt(-1);
                return;
            }

            int statusCount = statuses.Length;
            WriteInt(statusCount);

            for (int statusIndex = 0; statusIndex < statusCount; ++statusIndex) {
                WriteStatus(statuses[statusIndex]);
            }
        }

        private Tree ReadTree() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Tree(
                    ReadLong(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    (Faction) ReadEnum(), ReadDouble(), ReadInt(), ReadInt(), ReadStatuses()
            );
        }

        private void WriteTree(Tree tree) {
            if (tree == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(tree.Id);
            WriteDouble(tree.X);
            WriteDouble(tree.Y);
            WriteDouble(tree.SpeedX);
            WriteDouble(tree.SpeedY);
            WriteDouble(tree.Angle);
            WriteEnum((sbyte?) tree.Faction);
            WriteDouble(tree.Radius);
            WriteInt(tree.Life);
            WriteInt(tree.MaxLife);
            WriteStatuses(tree.Statuses);
        }

        private Tree[] ReadTrees() {
            int treeCount = ReadInt();
            if (treeCount < 0) {
                return previousTrees;
            }

            Tree[] trees = new Tree[treeCount];

            for (int treeIndex = 0; treeIndex < treeCount; ++treeIndex) {
                trees[treeIndex] = ReadTree();
            }

            return previousTrees = trees;
        }

        private void WriteTrees(Tree[] trees) {
            if (trees == null) {
                WriteInt(-1);
                return;
            }

            int treeCount = trees.Length;
            WriteInt(treeCount);

            for (int treeIndex = 0; treeIndex < treeCount; ++treeIndex) {
                WriteTree(trees[treeIndex]);
            }
        }

        private Wizard ReadWizard() {
            if (!ReadBoolean()) {
                return null;
            }

            return new Wizard(
                    ReadLong(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(), ReadDouble(),
                    (Faction) ReadEnum(), ReadDouble(), ReadInt(), ReadInt(), ReadStatuses(), ReadLong(), ReadBoolean(),
                    ReadInt(), ReadInt(), ReadDouble(), ReadDouble(), ReadInt(), ReadInt(), ReadEnums<SkillType>(),
                    ReadInt(), ReadInts(), ReadBoolean(), ReadMessages()
            );
        }

        private void WriteWizard(Wizard wizard) {
            if (wizard == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteLong(wizard.Id);
            WriteDouble(wizard.X);
            WriteDouble(wizard.Y);
            WriteDouble(wizard.SpeedX);
            WriteDouble(wizard.SpeedY);
            WriteDouble(wizard.Angle);
            WriteEnum((sbyte?) wizard.Faction);
            WriteDouble(wizard.Radius);
            WriteInt(wizard.Life);
            WriteInt(wizard.MaxLife);
            WriteStatuses(wizard.Statuses);
            WriteLong(wizard.OwnerPlayerId);
            WriteBoolean(wizard.IsMe);
            WriteInt(wizard.Mana);
            WriteInt(wizard.MaxMana);
            WriteDouble(wizard.VisionRange);
            WriteDouble(wizard.CastRange);
            WriteInt(wizard.Xp);
            WriteInt(wizard.Level);
            WriteEnums(wizard.Skills);
            WriteInt(wizard.RemainingActionCooldownTicks);
            WriteInts(wizard.RemainingCooldownTicksByAction);
            WriteBoolean(wizard.IsMaster);
            WriteMessages(wizard.Messages);
        }

        private Wizard[] ReadWizards() {
            int wizardCount = ReadInt();
            if (wizardCount < 0) {
                return null;
            }

            Wizard[] wizards = new Wizard[wizardCount];

            for (int wizardIndex = 0; wizardIndex < wizardCount; ++wizardIndex) {
                wizards[wizardIndex] = ReadWizard();
            }

            return wizards;
        }

        private void WriteWizards(Wizard[] wizards) {
            if (wizards == null) {
                WriteInt(-1);
                return;
            }

            int wizardCount = wizards.Length;
            WriteInt(wizardCount);

            for (int wizardIndex = 0; wizardIndex < wizardCount; ++wizardIndex) {
                WriteWizard(wizards[wizardIndex]);
            }
        }

        private World ReadWorld() {
            if (!ReadBoolean()) {
                return null;
            }

            return new World(
                    ReadInt(), ReadInt(), ReadDouble(), ReadDouble(), ReadPlayers(), ReadWizards(), ReadMinions(),
                    ReadProjectiles(), ReadBonuses(), ReadBuildings(), ReadTrees()
            );
        }

        private void WriteWorld(World world) {
            if (world == null) {
                WriteBoolean(false);
                return;
            }

            WriteBoolean(true);

            WriteInt(world.TickIndex);
            WriteInt(world.TickCount);
            WriteDouble(world.Width);
            WriteDouble(world.Height);
            WritePlayers(world.Players);
            WriteWizards(world.Wizards);
            WriteMinions(world.Minions);
            WriteProjectiles(world.Projectiles);
            WriteBonuses(world.Bonuses);
            WriteBuildings(world.Buildings);
            WriteTrees(world.Trees);
        }

        private World[] ReadWorlds() {
            int worldCount = ReadInt();
            if (worldCount < 0) {
                return null;
            }

            World[] worlds = new World[worldCount];

            for (int worldIndex = 0; worldIndex < worldCount; ++worldIndex) {
                worlds[worldIndex] = ReadWorld();
            }

            return worlds;
        }

        private void WriteWorlds(World[] worlds) {
            if (worlds == null) {
                WriteInt(-1);
                return;
            }

            int worldCount = worlds.Length;
            WriteInt(worldCount);

            for (int worldIndex = 0; worldIndex < worldCount; ++worldIndex) {
                WriteWorld(worlds[worldIndex]);
            }
        }

        private static void EnsureMessageType(MessageType actualType, MessageType expectedType) {
            if (actualType != expectedType) {
                throw new ArgumentException($"Received wrong message [actual={actualType}, expected={expectedType}].");
            }
        }

        private byte[] ReadByteArray(bool nullable) {
            int count = ReadInt();

            if (nullable) {
                if (count < 0) {
                    return null;
                }
            } else {
                if (count <= 0) {
                    return EmptyByteArray;
                }
            }

            return ReadBytes(count);
        }

        private void WriteByteArray(byte[] array) {
            if (array == null) {
                WriteInt(-1);
            } else {
                WriteInt(array.Length);
                WriteBytes(array);
            }
        }

        private sbyte? ReadEnum() {
            sbyte value = reader.ReadSByte();
            return value < 0 ? null : (sbyte?) value;
        }

        private E[] ReadEnums<E>() where E : struct, IComparable, IFormattable, IConvertible {
            int count = ReadInt();
            if (count < 0) {
                return null;
            }

            E[] enums = new E[count];

            for (int i = 0; i < count; ++i) {
                enums[i] = (E) Enum.ToObject(typeof(E), ReadEnum());
            }

            return enums;
        }

        private E[][] ReadEnums2D<E>() where E : struct, IComparable, IFormattable, IConvertible {
            int count = ReadInt();
            if (count < 0) {
                return null;
            }

            E[][] enums = new E[count][];

            for (int i = 0; i < count; ++i) {
                enums[i] = ReadEnums<E>();
            }

            return enums;
        }

        private E?[] ReadNullableEnums<E>() where E : struct, IComparable, IFormattable, IConvertible {
            int count = ReadInt();
            if (count < 0) {
                return null;
            }

            E?[] enums = new E?[count];

            for (int i = 0; i < count; ++i) {
                sbyte? value = ReadEnum();
                enums[i] = value.HasValue ? (E) Enum.ToObject(typeof(E), value) : (E?) null;
            }

            return enums;
        }

        private E?[][] ReadNullableEnums2D<E>() where E : struct, IComparable, IFormattable, IConvertible {
            int count = ReadInt();
            if (count < 0) {
                return null;
            }

            E?[][] enums = new E?[count][];

            for (int i = 0; i < count; ++i) {
                enums[i] = ReadNullableEnums<E>();
            }

            return enums;
        }

        private void WriteEnum(sbyte? value) {
            writer.Write(value ?? -1);
        }

        private void WriteEnums<E>(E[] enums) where E : struct, IComparable, IFormattable, IConvertible {
            if (enums == null) {
                WriteInt(-1);
                return;
            }

            int count = enums.Length;
            WriteInt(count);

            for (int i = 0; i < count; ++i) {
                WriteEnum(enums[i].ToSByte(null));
            }
        }

        private void WriteEnums2D<E>(E[][] enums) where E : struct, IComparable, IFormattable, IConvertible {
            if (enums == null) {
                WriteInt(-1);
                return;
            }

            int count = enums.Length;
            WriteInt(count);

            for (int i = 0; i < count; ++i) {
                WriteEnums(enums[i]);
            }
        }

        private void WriteEnums<E>(E?[] enums) where E : struct, IComparable, IFormattable, IConvertible {
            if (enums == null) {
                WriteInt(-1);
                return;
            }

            int count = enums.Length;
            WriteInt(count);

            for (int i = 0; i < count; ++i) {
                WriteEnum(enums[i]?.ToSByte(null));
            }
        }

        private void WriteEnums2D<E>(E?[][] enums) where E : struct, IComparable, IFormattable, IConvertible {
            if (enums == null) {
                WriteInt(-1);
                return;
            }

            int count = enums.Length;
            WriteInt(count);

            for (int i = 0; i < count; ++i) {
                WriteEnums(enums[i]);
            }
        }

        private string ReadString() {
            int length = ReadInt();
            if (length == -1) {
                return null;
            }

            return Encoding.UTF8.GetString(ReadBytes(length));
        }

        private void WriteString(string value) {
            if (value == null) {
                WriteInt(-1);
                return;
            }

            byte[] bytes = Encoding.UTF8.GetBytes(value);

            WriteInt(bytes.Length);
            WriteBytes(bytes);
        }

        private bool ReadBoolean() {
            return reader.ReadSByte() != 0;
        }

        private void WriteBoolean(bool value) {
            writer.Write((sbyte) (value ? 1 : 0));
        }

        private int ReadInt() {
            return reader.ReadInt32();
        }

        private int[] ReadInts() {
            int count = ReadInt();
            if (count < 0) {
                return null;
            }

            int[] ints = new int[count];

            for (int i = 0; i < count; ++i) {
                ints[i] = ReadInt();
            }

            return ints;
        }

        private int[][] ReadInts2D() {
            int count = ReadInt();
            if (count < 0) {
                return null;
            }

            int[][] ints = new int[count][];

            for (int i = 0; i < count; ++i) {
                ints[i] = ReadInts();
            }

            return ints;
        }

        private void WriteInt(int value) {
            writer.Write(value);
        }

        private void WriteInts(int[] ints) {
            if (ints == null) {
                WriteInt(-1);
                return;
            }

            int count = ints.Length;
            WriteInt(count);

            for (int i = 0; i < count; ++i) {
                WriteInt(ints[i]);
            }
        }

        private void WriteInts2D(int[][] ints) {
            if (ints == null) {
                WriteInt(-1);
                return;
            }

            int count = ints.Length;
            WriteInt(count);

            for (int i = 0; i < count; ++i) {
                WriteInts(ints[i]);
            }
        }

        private long ReadLong() {
            return reader.ReadInt64();
        }

        private void WriteLong(long value) {
            writer.Write(value);
        }

        private double ReadDouble() {
            return reader.ReadDouble();
        }

        private void WriteDouble(double value) {
            writer.Write(value);
        }

        private byte[] ReadBytes(int byteCount) {
            return reader.ReadBytes(byteCount);
        }

        private void WriteBytes(byte[] bytes) {
            writer.Write(bytes);
        }

        private enum MessageType {
            Unknown,
            GameOver,
            AuthenticationToken,
            TeamSize,
            ProtocolVersion,
            GameContext,
            PlayerContext,
            Moves
        }
    }
}