/* SCRIPT FOR DB SQL SERVER */


CREATE DATABASE jahia;

GO

USE [jahia]

GO

CREATE TABLE [dbo].[Activity](
	[id] [int] IDENTITY(1,1) NOT NULL,
	[name] [varchar](150) NULL,
	[distance] [float] NULL,
	[type] [varchar](50) NULL,
	[moving_time] [int] NULL,
	[start_date] [datetime] NULL,
 CONSTRAINT [PK_Activity] PRIMARY KEY CLUSTERED 
([id] ASC))

GO

INSERT INTO [dbo].[Activity] ([name] ,[distance] ,[type] ,[moving_time] ,[start_date]) VALUES ('name1', 363.56, 'Run',	   3245634,	'2016-04-08 15:33:59.567')
INSERT INTO [dbo].[Activity] ([name] ,[distance] ,[type] ,[moving_time] ,[start_date]) VALUES ('name2', 864.57, 'Cycle',  68745,	'2016-04-08 15:40:51.500')
INSERT INTO [dbo].[Activity] ([name] ,[distance] ,[type] ,[moving_time] ,[start_date]) VALUES ('name3', 644.58, 'Vehicle',875685,	'2016-04-08 15:41:44.507')

GO



