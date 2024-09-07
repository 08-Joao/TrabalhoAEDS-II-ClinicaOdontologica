package Utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class OrdenacaoPessoas {
    // Tamanhos dos campos para garantir que os registros tenham tamanhos fixos
    private static final int TAMANHO_ID = 4; // int
    private static final int TAMANHO_NOME = 100;
    private static final int TAMANHO_CPF = 15;
    private static final int TAMANHO_TELEFONE = 14;
    private static final int TAMANHO_DATA_NASCIMENTO = 10;
    private static final int TAMANHO_ENDERECO = 100;
    private static final int TAMANHO_PROFISSAO = 50;

    // Definindo o tamanho máximo de registros que podem ser mantidos na memória
    private static final int MAX_REGISTROS_MEMORIA = 200;

    // Tamanho total dos registros
    private static final int TAMANHO_REGISTRO_CLIENTE = TAMANHO_ID + TAMANHO_NOME + TAMANHO_CPF + TAMANHO_TELEFONE
            + TAMANHO_DATA_NASCIMENTO + TAMANHO_ENDERECO;
    private static final int TAMANHO_REGISTRO_PROFISSIONAL = TAMANHO_ID + TAMANHO_NOME + TAMANHO_CPF + TAMANHO_TELEFONE
            + TAMANHO_DATA_NASCIMENTO + TAMANHO_ENDERECO + TAMANHO_PROFISSAO;

    private static final int TAMANHO_HORARIO = 8; // long (para armazenar o horário em milissegundos)
    private static final int TAMANHO_REGISTRO_HORARIO = TAMANHO_HORARIO;

    private static final String CAMINHO_CLIENTES = "clientes.dat";
    private static final String CAMINHO_PROFISSIONAIS = "profissionais.dat";
    private static final String CAMINHO_HORARIOS = "horarios.dat";

    public static void ordenarClientes() throws IOException {
        ordenarDisco(CAMINHO_CLIENTES, TAMANHO_REGISTRO_CLIENTE, TAMANHO_ID);
    }

    public static void ordenarProfissionais() throws IOException {
        ordenarDisco(CAMINHO_PROFISSIONAIS, TAMANHO_REGISTRO_PROFISSIONAL, TAMANHO_ID);
    }

    private static void ordenarDisco(String arquivo, int tamanhoRegistro, int tamanhoCampoId) throws IOException {
        String arquivoTemp = "temp.dat";

        // Usando o mesmo arquivo para leitura e escrita
        try (RandomAccessFile raf = new RandomAccessFile(arquivo, "rwd")) {
            // Criar um arquivo temporário para armazenar dados durante a ordenação
            try (RandomAccessFile rafTemp = new RandomAccessFile(arquivoTemp, "rw")) {
                // Copiar dados do arquivo original para o arquivo temporário
                byte[] buffer = new byte[tamanhoRegistro];
                while (raf.read(buffer) != -1) {
                    rafTemp.write(buffer);
                }

                // Ordenar o arquivo temporário
                System.out.println("Ordenando arquivo de disco.");
                int quantidade = (int) (rafTemp.length() / tamanhoRegistro);
                quickSortDisco(rafTemp, 0, quantidade - 1, tamanhoRegistro, tamanhoCampoId);

                // Copiar dados do arquivo temporário para o arquivo original
                raf.seek(0); // Voltar ao início do arquivo original
                rafTemp.seek(0); // Voltar ao início do arquivo temporário
                while (rafTemp.read(buffer) != -1) {
                    raf.write(buffer);
                }
            }

            // Opcional: Excluir o arquivo temporário após a cópia
            File tempFile = new File(arquivoTemp);
            if (tempFile.delete()) {
                System.out.println("Arquivo temporário excluído.");
            } else {
                System.out.println("Não foi possível excluir o arquivo temporário.");
            }
        }
    }

    private static void quickSortDisco(RandomAccessFile raf, int esquerda, int direita, int tamanhoRegistro,
            int tamanhoCampoId) throws IOException {
        if (esquerda < direita) {
            int idPivo = obterId(raf, (esquerda + direita) / 2, tamanhoRegistro, tamanhoCampoId);
            int i = esquerda;
            int j = direita;

            while (i <= j) {
                while (obterId(raf, i, tamanhoRegistro, tamanhoCampoId) < idPivo && i < direita)
                    i++;
                while (obterId(raf, j, tamanhoRegistro, tamanhoCampoId) > idPivo && j > esquerda)
                    j--;

                if (i <= j) {
                    trocarTodosCampos(raf, i, j, tamanhoRegistro);
                    i++;
                    j--;
                }
            }

            if (esquerda < j)
                quickSortDisco(raf, esquerda, j, tamanhoRegistro, tamanhoCampoId);
            if (i < direita)
                quickSortDisco(raf, i, direita, tamanhoRegistro, tamanhoCampoId);
        }
    }

    private static void trocarTodosCampos(RandomAccessFile raf, long i, long j, int tamanhoRegistro)
            throws IOException {
        byte[] registroI = new byte[tamanhoRegistro];
        byte[] registroJ = new byte[tamanhoRegistro];

        // Ler o registro na posição i
        raf.seek(i * tamanhoRegistro);
        raf.readFully(registroI);

        // Ler o registro na posição j
        raf.seek(j * tamanhoRegistro);
        raf.readFully(registroJ);

        // Escrever o registro na posição j
        raf.seek(j * tamanhoRegistro);
        raf.write(registroI);

        // Escrever o registro na posição i
        raf.seek(i * tamanhoRegistro);
        raf.write(registroJ);
    }

    private static int obterId(RandomAccessFile raf, long registro, int tamanhoRegistro, int tamanhoCampoId)
            throws IOException {
        raf.seek(registro * tamanhoRegistro); // Posiciona-se no início do registro
        return raf.readInt(); // Lê o inteiro diretamente
    }

    // ----------------------------------------------------------------------NOVAS FUNÇÕES--------------------------
    public static void ordenarClientesNatural() throws IOException {
        String[] arquivosRuns = ordenacaoNatural("clientes.dat", 100, 4); 
        intercalarRuns("clientes_ordenados.dat", arquivosRuns, 100, 4); // Arquivo de saída final
    }

    public static void ordenarProfissionaisNatural() throws IOException {
        String[] arquivosRuns = ordenacaoNatural("CAMINHO_PROFISSIONAIS", 100, 4);
        intercalarRuns("profissionais_ordenados.dat", arquivosRuns, 100, 4); // Arquivo de saída final
    }

    // Método de ordenação natural
    private static String[] ordenacaoNatural(String arquivo, int tamanhoRegistro, int tamanhoCampoId)
            throws IOException {
        String tempPrefix = "temp_run_";
        RandomAccessFile raf = new RandomAccessFile(arquivo, "r");

        List<String> runFiles = new ArrayList<>();

        // Passo 1: Identificação e criação de runs naturais
        int runCounter = 0;
        while (raf.getFilePointer() < raf.length()) {
            String tempFileName = tempPrefix + runCounter++ + ".dat";
            try (RandomAccessFile tempRun = new RandomAccessFile(tempFileName, "rw")) {
                criarRunNatural(raf, tempRun, tamanhoRegistro, tamanhoCampoId);
                runFiles.add(tempFileName); // Armazena o nome do arquivo temporário
            }
        }

        raf.close();

        // Converte a lista de nomes de arquivos para array
        String[] arquivosRuns = runFiles.toArray(new String[0]);

        System.out.println("Ordenação natural concluída.");

        return arquivosRuns; // Retorna os nomes dos arquivos temporários para intercalação
    }

    // Método para criar um run natural
    private static void criarRunNatural(RandomAccessFile raf, RandomAccessFile tempRun, int tamanhoRegistro,
            int tamanhoCampoId) throws IOException {
        byte[] buffer = new byte[tamanhoRegistro];
        int ultimoId = Integer.MIN_VALUE;

        // Opcional: Limite de registros em memória
        int registrosEmMemoria = 0;

        while (raf.getFilePointer() < raf.length()) {
            long posicaoAtual = raf.getFilePointer();
            raf.read(buffer);
            int idAtual = obterIdFromBytes(buffer, tamanhoCampoId);

            if (idAtual >= ultimoId && registrosEmMemoria < MAX_REGISTROS_MEMORIA) {
                tempRun.write(buffer);
                ultimoId = idAtual;
                registrosEmMemoria++;
            } else {
                raf.seek(posicaoAtual); // Volta o ponteiro para a posição anterior
                break; // Fim do run natural
            }
        }
    }

    // Método para obter o ID a partir dos bytes
    private static int obterIdFromBytes(byte[] buffer, int tamanhoCampoId) {
        return ((buffer[0] & 0xFF) << 24) | ((buffer[1] & 0xFF) << 16) | ((buffer[2] & 0xFF) << 8) | (buffer[3] & 0xFF);
    }

    // Método de intercalação de runs
    public static void intercalarRuns(String arquivoSaida, String[] arquivosRuns, int tamanhoRegistro,
            int tamanhoCampoId) throws IOException {
        // Declara o array 'runs' no escopo do método
        Run[] runs = new Run[arquivosRuns.length];

        try (RandomAccessFile output = new RandomAccessFile(arquivoSaida, "rw")) {

            // Fila de prioridade para armazenar o menor registro de cada run
            PriorityQueue<RunRecord> pq = new PriorityQueue<>((r1, r2) -> Integer.compare(r1.id, r2.id));

            // Abre todos os arquivos dos runs e insere o primeiro registro de cada run na
            // fila
            for (int i = 0; i < arquivosRuns.length; i++) {
                runs[i] = new Run(arquivosRuns[i], tamanhoRegistro);
                if (runs[i].hasNext()) {
                    pq.add(runs[i].next());
                }
            }

            // Processa a fila de prioridade até que todos os runs tenham sido processados
            while (!pq.isEmpty()) {
                RunRecord menorRegistro = pq.poll();
                output.write(menorRegistro.registro); // Escreve o menor registro no arquivo de saída

                // Se o run de onde veio o menor registro ainda tiver mais registros, insere o
                // próximo
                if (menorRegistro.run.hasNext()) {
                    pq.add(menorRegistro.run.next());
                }
            }
        }

        // Limpa os arquivos temporários após a intercalação
        for (Run run : runs) {
            run.close();
            File tempFile = new File(run.fileName);
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }

        System.out.println("Intercalação concluída.");
    }

    // Classe auxiliar para representar um Run (subsequência)
    static class Run {
        String fileName;
        RandomAccessFile raf;
        int tamanhoRegistro;

        Run(String fileName, int tamanhoRegistro) throws IOException {
            this.fileName = fileName;
            this.raf = new RandomAccessFile(fileName, "r");
            this.tamanhoRegistro = tamanhoRegistro;
        }

        boolean hasNext() throws IOException {
            return raf.getFilePointer() < raf.length();
        }

        RunRecord next() throws IOException {
            byte[] buffer = new byte[tamanhoRegistro];
            raf.read(buffer);
            int id = obterIdFromBytes(buffer, 4); // Assume que o campo ID tem 4 bytes
            return new RunRecord(id, buffer, this);
        }

        void close() throws IOException {
            raf.close();
        }
    }

    // Classe auxiliar para representar um registro em um Run
    static class RunRecord implements Comparable<RunRecord> {
        int id;
        byte[] registro;
        Run run;

        RunRecord(int id, byte[] registro, Run run) {
            this.id = id;
            this.registro = registro;
            this.run = run;
        }

        @Override
        public int compareTo(RunRecord other) {
            return Integer.compare(this.id, other.id);
        }
    }

}
