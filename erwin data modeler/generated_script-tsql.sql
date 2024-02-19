use master
go
create database SAB_Projekat
go
use SAB_Projekat
go

CREATE TABLE [Article]
( 
	[IdA]                integer  IDENTITY  NOT NULL ,
	[Name]               nvarchar(100)  NOT NULL 
)
go

CREATE TABLE [Buyer]
( 
	[IdB]                integer  IDENTITY  NOT NULL ,
	[Balance]            decimal(10,3)  NOT NULL ,
	[Name]               nvarchar(100)  NOT NULL ,
	[IdC]                integer  NOT NULL 
)
go

CREATE TABLE [City]
( 
	[IdC]                integer  IDENTITY  NOT NULL ,
	[Name]               nvarchar(100)  NOT NULL 
)
go

CREATE TABLE [Delivery]
( 
	[LocPath]            nvarchar(3000)  NOT NULL ,
	[DaysLeft]           integer  NOT NULL ,
	[IdO]                integer  NOT NULL 
)
go

CREATE TABLE [HasArticles]
( 
	[IdA]                integer  NOT NULL ,
	[IdS]                integer  NOT NULL ,
	[Price]              integer  NOT NULL ,
	[Amount]             integer  NOT NULL 
)
go

CREATE TABLE [Item]
( 
	[IdO]                integer  NOT NULL ,
	[IdA]                integer  NOT NULL ,
	[IdS]                integer  NOT NULL ,
	[FinalArticlePrice]  decimal(10,3)  NOT NULL ,
	[Amount]             integer  NOT NULL ,
	[IdI]                integer  IDENTITY  NOT NULL ,
	[ArticlePrice]       decimal(10,3)  NOT NULL ,
	[ArticlePriceWithDiscount] decimal(10,3)  NOT NULL 
)
go

CREATE TABLE [Line]
( 
	[IdC1]               integer  NOT NULL ,
	[IdC2]               integer  NOT NULL ,
	[Distance]           integer  NOT NULL ,
	[IdL]                integer  IDENTITY  NOT NULL 
)
go

CREATE TABLE [Orders]
( 
	[IdO]                integer  IDENTITY  NOT NULL ,
	[Status]             nvarchar(100)  NOT NULL ,
	[IdB]                integer  NOT NULL 
)
go

CREATE TABLE [Shop]
( 
	[IdS]                integer  IDENTITY  NOT NULL ,
	[Name]               nvarchar(100)  NOT NULL ,
	[IdC]                integer  NOT NULL ,
	[Discount]           integer  NOT NULL ,
	[Balance]            decimal(10,3)  NOT NULL 
)
go

CREATE TABLE [TimeTable]
( 
	[IdTime]             integer  IDENTITY  NOT NULL ,
	[CurrTime]           date  NOT NULL 
)
go

CREATE TABLE [TransactionBuyer]
( 
	[IdB]                integer  NOT NULL ,
	[IdT]                integer  NOT NULL ,
	[TimeSen]            date  NOT NULL 
)
go

CREATE TABLE [Transactions]
( 
	[IdT]                integer  IDENTITY  NOT NULL ,
	[IdO]                integer  NOT NULL ,
	[Amount]             decimal(10,3)  NOT NULL 
)
go

CREATE TABLE [TransactionShop]
( 
	[IdS]                integer  NOT NULL ,
	[IdT]                integer  NOT NULL ,
	[TimeRec]            date  NOT NULL 
)
go

ALTER TABLE [Article]
	ADD CONSTRAINT [XPKArticle] PRIMARY KEY  CLUSTERED ([IdA] ASC)
go

ALTER TABLE [Buyer]
	ADD CONSTRAINT [XPKBuyer] PRIMARY KEY  CLUSTERED ([IdB] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([IdC] ASC)
go

ALTER TABLE [Delivery]
	ADD CONSTRAINT [XPKDelivery] PRIMARY KEY  CLUSTERED ([IdO] ASC)
go

ALTER TABLE [HasArticles]
	ADD CONSTRAINT [XPKHasArticles] PRIMARY KEY  CLUSTERED ([IdA] ASC,[IdS] ASC)
go

ALTER TABLE [Item]
	ADD CONSTRAINT [XPKItem] PRIMARY KEY  CLUSTERED ([IdI] ASC)
go

ALTER TABLE [Line]
	ADD CONSTRAINT [XPKLine] PRIMARY KEY  CLUSTERED ([IdL] ASC)
go

ALTER TABLE [Orders]
	ADD CONSTRAINT [XPKOrders] PRIMARY KEY  CLUSTERED ([IdO] ASC)
go

ALTER TABLE [Shop]
	ADD CONSTRAINT [XPKShop] PRIMARY KEY  CLUSTERED ([IdS] ASC)
go

ALTER TABLE [TimeTable]
	ADD CONSTRAINT [XPKSistem] PRIMARY KEY  CLUSTERED ([IdTime] ASC)
go

ALTER TABLE [TransactionBuyer]
	ADD CONSTRAINT [XPKTransactionBuyer] PRIMARY KEY  CLUSTERED ([IdT] ASC)
go

ALTER TABLE [Transactions]
	ADD CONSTRAINT [XPKTransaction] PRIMARY KEY  CLUSTERED ([IdT] ASC)
go

ALTER TABLE [TransactionShop]
	ADD CONSTRAINT [XPKTransactionShop] PRIMARY KEY  CLUSTERED ([IdT] ASC)
go


ALTER TABLE [Buyer]
	ADD CONSTRAINT [R_14] FOREIGN KEY ([IdC]) REFERENCES [City]([IdC])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Delivery]
	ADD CONSTRAINT [R_15] FOREIGN KEY ([IdO]) REFERENCES [Orders]([IdO])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [HasArticles]
	ADD CONSTRAINT [R_2] FOREIGN KEY ([IdA]) REFERENCES [Article]([IdA])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [HasArticles]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([IdS]) REFERENCES [Shop]([IdS])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Item]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([IdO]) REFERENCES [Orders]([IdO])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Item]
	ADD CONSTRAINT [R_8] FOREIGN KEY ([IdA],[IdS]) REFERENCES [HasArticles]([IdA],[IdS])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Line]
	ADD CONSTRAINT [R_4] FOREIGN KEY ([IdC1]) REFERENCES [City]([IdC])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Line]
	ADD CONSTRAINT [R_5] FOREIGN KEY ([IdC2]) REFERENCES [City]([IdC])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Orders]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([IdB]) REFERENCES [Buyer]([IdB])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Shop]
	ADD CONSTRAINT [R_1] FOREIGN KEY ([IdC]) REFERENCES [City]([IdC])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [TransactionBuyer]
	ADD CONSTRAINT [R_10] FOREIGN KEY ([IdB]) REFERENCES [Buyer]([IdB])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [TransactionBuyer]
	ADD CONSTRAINT [R_13] FOREIGN KEY ([IdT]) REFERENCES [Transactions]([IdT])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Transactions]
	ADD CONSTRAINT [R_9] FOREIGN KEY ([IdO]) REFERENCES [Orders]([IdO])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [TransactionShop]
	ADD CONSTRAINT [R_11] FOREIGN KEY ([IdS]) REFERENCES [Shop]([IdS])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [TransactionShop]
	ADD CONSTRAINT [R_12] FOREIGN KEY ([IdT]) REFERENCES [Transactions]([IdT])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


