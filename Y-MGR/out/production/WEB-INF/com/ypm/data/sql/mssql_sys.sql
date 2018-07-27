-- 创建全文索引
IF NOT EXISTS (SELECT * FROM sysfulltextcatalogs ftc WHERE ftc.name = N'@FULLTEXT')
CREATE FULLTEXT CATALOG [@FULLTEXT] WITH ACCENT_SENSITIVITY = ON
GO -- 创建配置信息表
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
SET ANSI_PADDING ON
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[Site_Config]') AND type in (N'U'))
BEGIN
CREATE TABLE [Site_Config](
	[Id] [varchar](50) NOT NULL,
	[Context] [nvarchar](max) NULL,
 CONSTRAINT [PK_Site_Config] PRIMARY KEY CLUSTERED ([Id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
END
SET ANSI_PADDING OFF
GO -- 创建定时器表
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
SET ANSI_PADDING ON
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[Site_Jobs]') AND type in (N'U'))
BEGIN
CREATE TABLE [Site_Jobs](
	[Id] [varchar](15) NOT NULL,
	[Sortid] [int] NOT NULL,
	[Zone] [int] NOT NULL,
	[Name] [nvarchar](50) NULL,
	[Sday] [varchar](20) NULL,
	[Eday] [varchar](20) NULL,
	[Outs] [varchar](max) NULL,
	[Times] [varchar](500) NULL,
	[Weeks] [varchar](10) NULL,
	[Stype] [tinyint] NOT NULL,
	[Script] [nvarchar](max) NULL,
	[Rlast] [bigint] NOT NULL,
	[Rnext] [bigint] NOT NULL,
	[Rtime] [bigint] NOT NULL,
	[Runs] [tinyint] NOT NULL,
	[State] [tinyint] NOT NULL,
 CONSTRAINT [PK_Site_Jobs] PRIMARY KEY CLUSTERED ([Id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
ALTER TABLE [Site_Jobs] ADD CONSTRAINT [DF_Site_Jobs_Sortid] DEFAULT ((0)) FOR [Sortid]
ALTER TABLE [Site_Jobs] ADD CONSTRAINT [DF_Site_Jobs_Zone] DEFAULT ((0)) FOR [Zone]
ALTER TABLE [Site_Jobs] ADD CONSTRAINT [DF_Site_Jobs_Stype] DEFAULT ((0)) FOR [Stype]
ALTER TABLE [Site_Jobs] ADD CONSTRAINT [DF_Site_Jobs_Rlast] DEFAULT ((0)) FOR [Rlast]
ALTER TABLE [Site_Jobs] ADD CONSTRAINT [DF_Site_Jobs_Rnext] DEFAULT ((0)) FOR [Rnext]
ALTER TABLE [Site_Jobs] ADD CONSTRAINT [DF_Site_Jobs_Rtime] DEFAULT ((0)) FOR [Rtime]
ALTER TABLE [Site_Jobs] ADD CONSTRAINT [DF_Site_Jobs_Runs] DEFAULT ((0)) FOR [Runs]
ALTER TABLE [Site_Jobs] ADD CONSTRAINT [DF_Site_Jobs_State] DEFAULT ((0)) FOR [State]
END
SET ANSI_PADDING OFF
GO -- 创建导航菜单表
SET ANSI_NULLS ON
SET QUOTED_IDENTIFIER ON
SET ANSI_PADDING ON
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[Site_Menu]') AND type in (N'U'))
BEGIN
CREATE TABLE [Site_Menu](
	[Id] [int] NOT NULL,
	[Tid] [int] NOT NULL,
	[Sortid] [smallint] NOT NULL,
	[Name] [nvarchar](50) NULL,
	[Url] [varchar](50) NULL,
	[Alt] [nvarchar](50) NULL,
	[Opens] [varchar](50) NULL,
	[Style] [varchar](50) NULL,
	[State] [tinyint] NOT NULL,
 CONSTRAINT [PK_Site_Menu] PRIMARY KEY CLUSTERED ([Id] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
ALTER TABLE [Site_Menu] ADD CONSTRAINT [DF_Site_Menu_Tid] DEFAULT ((0)) FOR [Tid]
ALTER TABLE [Site_Menu] ADD CONSTRAINT [DF_Site_Menu_Sortid] DEFAULT ((0)) FOR [Sortid]
ALTER TABLE [Site_Menu] ADD CONSTRAINT [DF_Site_Menu_State] DEFAULT ((0)) FOR [State]
END
SET ANSI_PADDING OFF
GO -- 创建附加索引
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE object_id = OBJECT_ID(N'[Site_Menu]') AND name = N'PS_Site_Menu')
CREATE NONCLUSTERED INDEX [PS_Site_Menu] ON [Site_Menu] ([Tid] ASC,[Sortid] ASC) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO -- 创建邮件服务表

