# LoginBonusPlugin

Minecraft Spigotプラグイン。プレイヤーの連続ログイン（ストリーク）をトラックし、条件を満たすと報酬アイテムを与えるものです。設定可能なボーナス、ボスバー進捗表示、MySQLデータベース対応、動的コマンド登録機能を搭載しています。

## 機能

- **ログイン報酬**: 1日に60分以上プレイした場合のみ受け取り。ストリーク日数分アイテムが増加。
- **スペシャル報酬**:
    - 特定日数（例: 7日）に到達で追加報酬。
    - 倍数日数（例: 5,10日）に到達で追加報酬。
- **ボスバー**: ログイン進捗をリアルタイム表示。色・スタイルカスタマイズ可能。
- **インベントリ判定**: 空き不足時は付与せず案内メッセージを表示。
- **ストレージ**: YAMLファイルまたはMySQLデータベースを選択可能。
- **動的コマンド登録**: command.ymlでコマンドを自由に設定・変更可能。
- **権限管理**: LuckPerms対応（loginbonus.admin）。

## インストール

1. [Spigot](https://spigotmc.org/) サーバーを準備（Minecraft 1.19.4以上推奨）。
2. ダウンロードした `LoginBonusPlugin.jar` を `plugins/` フォルダに配置。
3. サーバーを起動。プラグインフォルダ `plugins/LoginBonusPlugin/` が自動作成されます。
4. `config.yml` と `command.yml` を編集（詳細は下記）。
5. サーバーを再起動するか `/loginbonus reload` で再読み込み。

### 依存関係
- MySQLを使用する場合: `mysql-connector-java:8.0.33` をサーバーに追加（build.gradleで自動）。

## 設定

### config.yml
プラグインのメイン設定ファイル。`plugins/LoginBonusPlugin/config.yml`

```yaml
# ストレージ設定
storage:
  type: "yaml"  # "yaml" または "mysql"
  mysql:
    host: "localhost"
    port: 3306
    database: "minecraft"
    username: "root"
    password: "password"

# ログイン報酬アイテム (ストリーク日数分与える)
login-rewards:
  - material: DIAMOND
    amount: 1

# 必要プレイ時間 (分)
required-playtime-minutes: 60

# 特定日数報酬設定
special-streak-rewards-enabled: false
special-streak-rewards:
  '7':
    items:
      - type: EMERALD
        amount: 7
    message: "&b7日連続ログイン！エメラルド7個プレゼント！"

# 倍数日数報酬設定
multiples-enabled: true
multiples:
  '5':
    items:
      - type: GOLD_INGOT
        amount: 2
    message: "&e%days%日連続ログイン！ゴールドインゴット2個プレゼント！"

# ボスバー設定
bossbar-color: GREEN
bossbar-style: SOLID

# メッセージ設定 (装飾コード使用可能)
messages:
  no-permission: "&cこのコマンドを使用する権限がありません。"
  insufficient-playtime: "&e今日のプレイ時間が足りません (&c%required%&e 分必要です)。"
  inventory-full: "&cインベントリが満杯です。報酬種類数以上の空きスロット (&e%slots%&c 個)が必要です。&e/receivebonus &cで受け取れます。"
  bonus-received: "&aログインボーナスを受け取りました！"
  special-bonus-received: "&6%days% 日分のスペシャルボーナスを受け取りました！"
  bossbar-progress: "&eログインボーナス進捗: &a%current%&e/&a%required% &e分"
  bossbar-claimable: "&aログインボーナス受け取り可能！"
  bossbar-claimed: "&7ログインボーナス受け取り済み"
```

### command.yml
コマンド設定ファイル。`plugins/LoginBonusPlugin/command.yml`

```yaml
commands:
  receivebonus:
    description: "その日のプレイ時間条件を満たした場合、当日のlogin報酬を受け取る"
    usage: "/receivebonus"
    permission: "loginbonus.receive"
    aliases: ["rb", "bonus"]
  forcereward:
    description: "指定プレイヤーに指定日数分のスペシャル報酬＋同日数分のlogin報酬を即時付与"
    usage: "/forcereward <player> <days>"
    permission: "loginbonus.admin"
    aliases: ["fr"]
  setstreak:
    description: "指定プレイヤーのストリーク（連続ログイン日数）を強制編集"
    usage: "/setstreak <player> <days>"
    permission: "loginbonus.admin"
    aliases: ["ss"]
  loginbonus:
    description: "ログインボーナスの管理者コマンド"
    usage: "/loginbonus <reload|forcegive|resetplaytime> <player>"
    permission: "loginbonus.admin"
    aliases: ["lb"]
```

## コマンド

### 一般ユーザーコマンド
- `/receivebonus`: 今日の報酬を受け取る（条件: 60分以上プレイ、インベントリ空きあり）。

### 管理者コマンド（権限: loginbonus.admin）
- `/forcereward <player> <days>`: 指定プレイヤーに指定日数分の報酬を強制付与。
- `/setstreak <player> <days>`: 指定プレイヤーのストリークを編集。
- `/loginbonus reload`: 設定ファイルを再読み込み。
- `/loginbonus forcegive <player>`: 指定プレイヤーに今日の報酬を強制付与。
- `/loginbonus resetplaytime <player>`: 指定プレイヤーのプレイ時間をリセット。

## 権限

- `loginbonus.receive`: 報酬受け取りコマンド使用権限（デフォルト: true）。
- `loginbonus.admin`: 管理者コマンド使用権限（デフォルト: false、OP推奨）。

LuckPermsなどで割り当ててください。

## 使用例

1. プレイヤーがサーバーに参加すると、ボスバーに進捗が表示されます。
2. 60分プレイすると、ボスバーが「受け取り可能」に変わります。
3. `/receivebonus` で報酬を受け取る。
4. ストリーク7日に達すると、スペシャル報酬が追加でドロップ。

## トラブルシューティング

- **MySQL接続エラー**: config.ymlのMySQL設定を確認。データベースとテーブルを作成してください。
- **コマンドが認識されない**: command.ymlを編集後、サーバー再起動。
- **報酬が付与されない**: プレイ時間を確認（`/loginbonus resetplaytime` でリセット可能）。

## ライセンス

MIT License。自由に使用・改変してください。

## サポート

バグや質問はGitHub Issuesで報告してください。