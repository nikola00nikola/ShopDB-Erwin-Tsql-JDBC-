use SAB_Projekat

go

create procedure SP_FINAL_PRICE
@OrderId int,
@Price decimal(10, 3) output
as
begin

declare @Today date

select @Today = CurrTime from TimeTable

set @Price = 0
declare @IdA int
declare @IdS int
declare @Amount int


declare @PriceA int
declare @Discount int
declare @PriceAFinal decimal(10, 3)


declare @IdB int
declare @factor decimal(10, 3)
set @factor = 1

select @IdB = IdB from Orders where IdO = @OrderId

if ((select sum(T.Amount) from Transactions T join TransactionBuyer B on T.IdT = B.IdT where B.IdB = @IdB and DATEDIFF(DAY, B.TimeSen, @Today) < 30
) >= 10000)
begin
	set @factor = 0.98
end


declare @cursor cursor

set @cursor = cursor for	select IdA, IdS, Amount from Item where IdO = @OrderId
open @cursor
fetch next from @cursor into @IdA, @IdS, @Amount

while @@FETCH_STATUS = 0
begin
	select @PriceA = Price from HasArticles where IdA = @IdA and IdS = @IdS
	select @Discount = Discount from Shop where IdS = @IdS

	set @PriceAFinal = 1.0 * @PriceA * @Amount * (100 - @Discount) / 100
	set @Price = @Price + @PriceAFinal

	update Item set FinalArticlePrice = @PriceAFinal * @factor, ArticlePrice = @PriceA * @Amount, ArticlePriceWithDiscount = @PriceAFinal  where IdA = @IdA and IdS = @IdS and IdO = @OrderId


	fetch next from @cursor into @IdA, @IdS, @Amount
end

set @Price = @Price * @factor


close @cursor
deallocate @cursor


end

go

CREATE TRIGGER TR_TRANSFER_MONEY_TO_SHOPS
ON Orders
FOR update
As
Begin
	declare @Today date

	select @Today = CurrTime from TimeTable
	declare @IdO int
	declare @Status nvarchar(10)

	if (select count(*) from inserted) > 1
	begin
		return
	end

	select @IdO = IdO, @Status = Status from inserted

	if (@Status != 'recieved')
	begin
		return
	end
	
	declare @amount decimal(10,3)
	declare @IdS int

	declare @cursor cursor
	set @cursor = cursor for select IdS, sum(ArticlePriceWithDiscount) from Item where IdO = @IdO group by IdS

	open @cursor

	fetch next from @cursor into @IdS, @Amount
	declare @IdT int
	while @@FETCH_STATUS = 0
	begin
		insert into Transactions(IdO, Amount) values( @IdO, 0.95 * @Amount)
		select @IdT =  max(IdT) from Transactions
		insert into TransactionShop(IdS, TimeRec, IdT) values (@IdS, @Today, @IdT)


		fetch next from @cursor into @IdS, @Amount
	end

	close @cursor
	deallocate @cursor
end