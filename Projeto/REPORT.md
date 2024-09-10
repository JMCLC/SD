			**Relatório de SD**


- O objetivo desta terceira entrega foi a criação se um sistema distríbuido de 2 servidores e 1 cliente(com extensão para 3 servidores).

- Para este objetivo foram utilizados vector clocks tanto do lado do server como do client da seguinte forma:
	
	- Foram alterados os ficheiros .proto para incluírem os vector clocks nos pedidos e respostas.

	- Do lado do client, é criado um vector clock para o client que é enviado com cada pedido que é feito ao servidor
	, este vector clock, leva update consoante necessário e é devolvido um novo vector clock atualizado na resposta do server ao client.

	- Do lado do server, são criados dois vector clocks no ServerState, um denominado por replicaTS que é aumentado cada vez que uma operação
	é adicionada uma operação ao ledger, e o segundo denominada por valueTS aumenta apenas quando uma operação é executada. Sendo assim, o 
	replicaTS é o que é comparado ao vector clock que é recebido pelo client e enviado para o cliente após a comparação.

	- Do lado do server, é implementada a função de propogate para dar update nos legders de outros servers e verificar que todos estão atualizados.
	Para este feito um server A envia o seu ledger juntamente com o seu replicaTS para serem comparadas as timestamps das operações do ledger de A com
	as timestamps no vector clock do server B (replicaTS). Caso se dê um update, o server B irá altera o seu valueTS dando merge com a timestamp da
	operação em questão.

	- Quando um servidor é criado com o qualifier C, este envia um pedido para aumentar o tamanho de todos os vector clocks dos servidores que estao registados no NamingServer. Com este pedido todos os servidores vão ter as timestamps dos seus valueTS e replicaTs, e todas as operações que estão guardadas no seu ledger aumentadas.
