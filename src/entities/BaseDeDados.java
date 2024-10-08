package entities;

// Importa classes necessárias para manipulação de arquivos, datas e listas
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

import utils.CaminhoArquivo;
import utils.GeneralUsage;
import utils.TamanhoCampo;

public class BaseDeDados {

	private static final int TAMANHO_ID = TamanhoCampo.ID.valor;
    private static final int TAMANHO_NOME = TamanhoCampo.NOME.valor;;
    private static final int TAMANHO_CPF = TamanhoCampo.CPF.valor;
    private static final int TAMANHO_TELEFONE = TamanhoCampo.TELEFONE.valor;
    private static final int TAMANHO_DATA_NASCIMENTO = TamanhoCampo.DATA_NASCIMENTO.valor;
    private static final int TAMANHO_ENDERECO = TamanhoCampo.ENDERECO.valor;
    private static final int TAMANHO_PROFISSAO = TamanhoCampo.PROFISSAO.valor;
    private static final int POSICAO = TamanhoCampo.POSICAO.valor;
    
    
    private static final int TAMANHO_REGISTRO_CLIENTE = TamanhoCampo.REGISTRO_CLIENTE.valor;
    private static final int TAMANHO_REGISTRO_PROFISSIONAL = TamanhoCampo.REGISTRO_PROFISSIONAL.valor;

    private static final int TAMANHO_HORARIO = TamanhoCampo.HORARIO.valor;
    private static final int TAMANHO_LISTA_AGENDADOS = TamanhoCampo.LISTA_AGENDADOS.valor; 
    private static final int TAMANHO_TOTAL_AGENDAMENTOS = TamanhoCampo.TOTAL_AGENDAMENTOS.valor; 
    private static final int TAMANHO_REGISTRO_HORARIO = TamanhoCampo.REGISTRO_HORARIO.valor;

  
    private static final String CAMINHO_CLIENTES = CaminhoArquivo.CLIENTES.caminho;
    private static final String CAMINHO_PROFISSIONAIS = CaminhoArquivo.PROFISSIONAIS.caminho;
    private static final String CAMINHO_HORARIOS = CaminhoArquivo.HORARIOS.caminho;

    
    // ATIVIDADE AVALIATIVA 3
    
    

    
    
	public static Hashtable<Integer, Integer> cHt = new Hashtable(); // Hash Table da base de Clientes
	public static Hashtable<Integer, Integer> pHt = new Hashtable(); // Hash Table da base de Profissionais
	public static Hashtable<Date, Integer> hHt = new Hashtable(); // Hash Table da base dos Horários
	
	
	public static void mapearHash(String type) {
		Integer count = 1;
		if(type.equalsIgnoreCase("Cliente")) {
			try (DataInputStream disClientes = new DataInputStream(new FileInputStream(CAMINHO_CLIENTES))) {
				while (disClientes.available() > 0) {
					Cliente cliente = lerRegistroCliente(disClientes);
					cHt.put(cliente.getId(), count);
					count++;
				}							
			} catch (IOException e) {
				System.err.println("Erro ao manipular o arquivo de clientes: " + e.getMessage());
			}
		}else if(type.equalsIgnoreCase("Profissional")) {
			try (DataInputStream disProfissionais = new DataInputStream(new FileInputStream(CAMINHO_PROFISSIONAIS))) {
				while (disProfissionais.available() > 0) {
					Profissional profissional = lerRegistroProfissional(disProfissionais);
					pHt.put(profissional.getId(), count);
					count++;
				}
			} catch (IOException e) {
				System.err.println("Erro ao manipular o arquivo de clientes: " + e.getMessage());
			}
		}else if(type.equalsIgnoreCase("Horario")) {
			try (DataInputStream disHorarios = new DataInputStream(new FileInputStream(CAMINHO_HORARIOS))) {
				while (disHorarios.available() > 0) {
					Horario horario = lerRegistroHorario(disHorarios);
					hHt.put(horario.getHorario(), count);
					count++;
				}
			} catch (IOException e) {
				System.err.println("Erro ao manipular o arquivo de clientes: " + e.getMessage());
			}
		}
	}
	
	public static Integer getPosHash(String type, Integer Id) {
		if(type.compareToIgnoreCase("Cliente") == 0) {
			return cHt.get(Id);
		}else if(type.compareToIgnoreCase("Profissional") == 0) {
			return pHt.get(Id);
		}
		return null;
	}
	
	public static Integer getPosHorarioHash(Date horario) {
		return hHt.get(horario);
	}
	
	public static void hashAdd(String type, Integer Id) {
		if(type.equalsIgnoreCase("Cliente")) {
			cHt.put(Id, cHt.size() + 1);
		}else if(type.equalsIgnoreCase("Profissional")) {
			pHt.put(Id, pHt.size() + 1);
		}
	}
	
	
	public static void hashRemove(String type, Integer Id) {
		Integer pos;
		
		if(type.equalsIgnoreCase("Cliente")) {
			pos = cHt.remove(Id);
			if (pos != null) {
		        // Decrementa todas as posições superiores
		        cHt.forEach((hashId, hashPos) -> {
		            if (hashPos > pos) {
		            	cHt.put(hashId, hashPos - 1); // Decrementa a posição
		            }
		        });
		    }
		}else if(type.equalsIgnoreCase("Profissional")) {
			pos = pHt.remove(Id);			
			if (pos != null) {
		        // Decrementa todas as posições superiores
				pHt.forEach((hashId, hashPos) -> {
		            if (hashPos > pos) {
		            	pHt.put(hashId, hashPos - 1); // Decrementa a posição
		            }
		        });
		    }
		}
		
	}

	
	public static void imprimeHash() {
		cHt.forEach((chave, valor) -> {
		    System.out.println("Id: " + chave + ", Posicao: " + valor);
		});
	}
    
	public static void criarBasesDesordenada(String type, int size, TabelaHashDisco tabelaHash) {
		List<Cliente> clientes = new ArrayList<>();
		List<Profissional> profissionais = new ArrayList<>();
		Random random = new Random();

		// Se o tipo for "Cliente", gera registros desordenados de clientes
		if (type.compareToIgnoreCase("Cliente") == 0) {
			for (int i = 1; i <= size; i++) {
				Cliente cliente = new Cliente(i, GeneralUsage.gerarNome(), GeneralUsage.gerarCPF(),
						GeneralUsage.gerarTelefone(), GeneralUsage.gerarDataNascimento(false),
						GeneralUsage.gerarEndereco(),-1); // Cria um cliente
				clientes.add(cliente);
			}

			Collections.shuffle(clientes); // Embaralha a lista de clientes

			try (DataOutputStream dosClientes = new DataOutputStream(new FileOutputStream(CAMINHO_CLIENTES))) {
				for (Cliente cliente : clientes) {					
					escreverRegistroCliente(dosClientes, cliente);
				}		
//				tabelaHash.mapearClientesParaHash();
			} catch (IOException e) {
				System.err.println("Erro ao manipular o arquivo de clientes: " + e.getMessage());
			}
		// Se o tipo for "Profissional", gera registros desordenados de profissionais
		} else if (type.compareToIgnoreCase("Profissional") == 0) {
			for (int i = 1; i <= size; i++) {
				Profissional profissional = new Profissional(i, GeneralUsage.gerarNome(), GeneralUsage.gerarCPF(),
						GeneralUsage.gerarTelefone(), GeneralUsage.gerarDataNascimento(true),
						GeneralUsage.gerarEndereco(), GeneralUsage.gerarProfissao()); // Cria um profissional
				profissionais.add(profissional);
			}

			Collections.shuffle(profissionais); // Embaralha a lista de profissionais

			try (DataOutputStream dosProfissionais = new DataOutputStream(
					new FileOutputStream(CAMINHO_PROFISSIONAIS))) {
				for (Profissional profissional : profissionais) {
					escreverRegistroProfissional(dosProfissionais, profissional);
				}
			} catch (IOException e) {
				System.err.println("Erro ao manipular o arquivo de profissionais: " + e.getMessage());
			}
		}
	}

	// Cria uma base de horários desordenada
	public static void criarBaseHorariosDesordenada() throws FileNotFoundException, IOException {
		List<Horario> horarios = new ArrayList<>();
		Date dataInicial = GeneralUsage.obterDataInicial();
		Calendar calendario = Calendar.getInstance();
		calendario.setTime(dataInicial);

		// Preenche a lista de horários com intervalos de 30 minutos em dias úteis
		while (calendario.getTime().before(GeneralUsage.obterDataFinal())) {
			if (calendario.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
					&& calendario.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
				Calendar horaAtual = (Calendar) calendario.clone();
				horaAtual.set(Calendar.HOUR_OF_DAY, 8);
				horaAtual.set(Calendar.MINUTE, 0);

				while (horaAtual.get(Calendar.HOUR_OF_DAY) < 18) {
					// Cria um novo objeto Horario para cada intervalo de 30 minutos
					Horario horarioObj = new Horario(horaAtual.getTime());

					// Verifica se o horário já foi adicionado
					if (!horarios.contains(horarioObj)) {
						horarios.add(horarioObj);
					}

					horaAtual.add(Calendar.MINUTE, 30); // Incrementa 30 minutos
				}
			}
			calendario.add(Calendar.DAY_OF_MONTH, 1); // Move para o próximo dia
		}

		Collections.shuffle(horarios); // Embaralha a lista de horários

		try (DataOutputStream dosHorarios = new DataOutputStream(new FileOutputStream(CAMINHO_HORARIOS))) {
			for (Horario horario : horarios) {
				escreverRegistroHorario(dosHorarios, horario);
			}
		} catch (IOException e) {
			System.err.println("Erro ao criar base de horários: " + e.getMessage());
		}
	}

	// Função para escrever um cliente em um arquivo com tamanho fixo
	public static void escreverRegistroCliente(DataOutputStream dos, Cliente cliente) throws IOException {
		dos.writeInt(cliente.getId());
		dos.write(cliente.getNome().getBytes(StandardCharsets.UTF_8), 0,
				Math.min(TAMANHO_NOME, cliente.getNome().length()));
		dos.write(cliente.getCpf().getBytes(StandardCharsets.UTF_8), 0,
				Math.min(TAMANHO_CPF, cliente.getCpf().length()));
		dos.write(cliente.getTelefone().getBytes(StandardCharsets.UTF_8), 0,
				Math.min(TAMANHO_TELEFONE, cliente.getTelefone().length()));
		dos.write(cliente.getDataNascimento().getBytes(StandardCharsets.UTF_8), 0,
				Math.min(TAMANHO_DATA_NASCIMENTO, cliente.getDataNascimento().length()));
		dos.write(cliente.getEndereco().getBytes(StandardCharsets.UTF_8), 0,
				Math.min(TAMANHO_ENDERECO, cliente.getEndereco().length()));

		int bytesEscritos = TAMANHO_ID + TAMANHO_NOME + TAMANHO_CPF + TAMANHO_TELEFONE + TAMANHO_DATA_NASCIMENTO
				+ TAMANHO_ENDERECO;
		for (int i = bytesEscritos; i < TAMANHO_REGISTRO_CLIENTE; i++) {
			dos.writeByte(' ');
		}
	}

	// Função para escrever um profissional em um arquivo com tamanho fixo
	public static void escreverRegistroProfissional(DataOutputStream dos, Profissional profissional)
			throws IOException {
		dos.writeInt(profissional.getId());
		dos.write(profissional.getNome().getBytes(StandardCharsets.UTF_8), 0,
				Math.min(TAMANHO_NOME, profissional.getNome().length()));
		dos.write(profissional.getCpf().getBytes(StandardCharsets.UTF_8), 0,
				Math.min(TAMANHO_CPF, profissional.getCpf().length()));
		dos.write(profissional.getTelefone().getBytes(StandardCharsets.UTF_8), 0,
				Math.min(TAMANHO_TELEFONE, profissional.getTelefone().length()));
		dos.write(profissional.getDataNascimento().getBytes(StandardCharsets.UTF_8), 0,
				Math.min(TAMANHO_DATA_NASCIMENTO, profissional.getDataNascimento().length()));
		dos.write(profissional.getEndereco().getBytes(StandardCharsets.UTF_8), 0,
				Math.min(TAMANHO_ENDERECO, profissional.getEndereco().length()));
		dos.write(profissional.getProfissao().getBytes(StandardCharsets.UTF_8), 0,
				Math.min(TAMANHO_PROFISSAO, profissional.getProfissao().length()));

		int bytesEscritos = TAMANHO_ID + TAMANHO_NOME + TAMANHO_CPF + TAMANHO_TELEFONE + TAMANHO_DATA_NASCIMENTO
				+ TAMANHO_ENDERECO + TAMANHO_PROFISSAO;
		for (int i = bytesEscritos; i < TAMANHO_REGISTRO_PROFISSIONAL; i++) {
			dos.writeByte(' ');
		}
	}

	static Cliente lerRegistroCliente(DataInputStream dis) throws IOException {
		int id = dis.readInt();
		String nome = lerCampo(dis, TAMANHO_NOME);
		String cpf = lerCampo(dis, TAMANHO_CPF);
		String telefone = lerCampo(dis, TAMANHO_TELEFONE);
		String dataNascimento = lerCampo(dis, TAMANHO_DATA_NASCIMENTO);
		String endereco = lerCampo(dis, TAMANHO_ENDERECO);
		String posicao = lerCampo(dis, POSICAO);
		return new Cliente(id, nome, cpf, telefone, dataNascimento, endereco,posicao);
	}

	private static Profissional lerRegistroProfissional(DataInputStream dis) throws IOException {
		int id = dis.readInt();
		String nome = lerCampo(dis, TAMANHO_NOME);
		String cpf = lerCampo(dis, TAMANHO_CPF);
		String telefone = lerCampo(dis, TAMANHO_TELEFONE);
		String dataNascimento = lerCampo(dis, TAMANHO_DATA_NASCIMENTO);
		String endereco = lerCampo(dis, TAMANHO_ENDERECO);
		String profissao = lerCampo(dis, TAMANHO_PROFISSAO);
		return new Profissional(id, nome, cpf, telefone, dataNascimento, endereco, profissao);
	}

	public static void imprimirBaseDeDados(String type) {

		if (type.compareToIgnoreCase("Cliente") == 0) {
			System.out.println("\n\n\nImprimindo Base dos Clientes:");
			try (DataInputStream disClientes = new DataInputStream(new FileInputStream(CAMINHO_CLIENTES))) {
				while (disClientes.available() > 0) {
					Cliente cliente = lerRegistroCliente(disClientes);
					cliente.imprimeInformacao();
				}
			} catch (IOException e) {
				System.err.println("Erro ao manipular o arquivo de clientes: " + e.getMessage());
			}

		} else if (type.compareToIgnoreCase("Profissional") == 0) {
			System.out.println("\n\n\nImprimindo Base dos Profissionais:");
			try (DataInputStream disProfissionais = new DataInputStream(new FileInputStream(CAMINHO_PROFISSIONAIS))) {
				while (disProfissionais.available() > 0) {
					Profissional profissional = lerRegistroProfissional(disProfissionais);
					profissional.imprimeInformacao();
				}
			} catch (IOException e) {
				System.err.println("Erro ao manipular o arquivo de profissionais: " + e.getMessage());
			}
		}
	}

	public static void escreverRegistroHorario(DataOutputStream dos, Horario horario) throws IOException {
		dos.writeLong(horario.getHorario().getTime());
		List<ListaAgendados> agendamentos = horario.getAgendamentos();
		for (ListaAgendados agendamento : agendamentos) {
			dos.writeInt(agendamento.getId_cliente());
			dos.writeInt(agendamento.getId_profissional());
		}
	}

	private static Horario lerRegistroHorario(DataInputStream dis) throws IOException {
		long horarioMillis = dis.readLong();
		Date horario = new Date(horarioMillis);
		Horario horarioObj = new Horario(horario);

		// Inicializa a lista de agendamentos com tamanho fixo
		List<ListaAgendados> agendamentos = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			int idCliente = dis.readInt();
			int idProfissional = dis.readInt();
			ListaAgendados agendamento = new ListaAgendados(idCliente, idProfissional);
			agendamentos.add(agendamento);
		}
		horarioObj.setAgendamentos(agendamentos);

		return horarioObj;
	}

	public static void imprimirBaseDeHorarios() {
		System.out.println("\n\n\nImprimindo Base de Horários:");
		try (DataInputStream disHorarios = new DataInputStream(new FileInputStream(CAMINHO_HORARIOS))) {
			while (disHorarios.available() > 0) {
				Horario horario = lerRegistroHorario(disHorarios);
				horario.imprimeInformacao();
			}
		} catch (IOException e) {
			System.err.println("Erro ao manipular o arquivo de horários:" + e.getMessage());
		}
	}

	private static String lerCampo(DataInputStream dis, int tamanhoCampo) throws IOException {
		byte[] buffer = new byte[tamanhoCampo];
		dis.readFully(buffer);
		return new String(buffer, StandardCharsets.UTF_8).trim();
	}
}