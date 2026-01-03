package com.TSgames.SobreVivencia;


import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends Activity {
    private TextView txtMain;
    private LinearLayout buttonLayout;
    private ScrollView scrollView;
    private Random random = new Random();

    private static final String PREFS_NAME = "SobrevivenciaPrefs";
    private static final String MUERTE_FILE = "muerte_previa.dat";

    private int dia = 1;
    private int tiempoMinutos = 360;
    private int fotosRestantes = 24;
    private int integridadCamara = 100;
    private int claridadVision = 70;
    private boolean enCampamento = true;
    private boolean tieneRefugio = false;
    private boolean modoDesesperacion = false;
    private boolean alucinando = false;
    private int diasSinAntibiotico = 0;
    private int hambre = 50;
    private int sed = 50;
    private int moral = 50;
    private int temperaturaCorporal = 37;
    private boolean tieneFuego = false;
    private boolean heridasInspeccionadasHoy = false;
    private boolean recolectadoHoy = false;
    private boolean tormentaActiva = false;
    private boolean animalCerca = false;
    private boolean infeccionActiva = false;

    private final int[] salud = {80, 100, 60, 100, 100, 100};
    private static final int BRAZO_IZQ = 0;
    private static final int BRAZO_DER = 1;
    private static final int PIERNAS = 2;
    private static final int TORSO = 3;
    private static final int CABEZA = 4;
    private static final int PIES = 5;

    private final List<String> inventario = new ArrayList<String>();
    private final List<String> diarioPaginas = new ArrayList<String>();
    private final Map<String, String> mesaCrafting = new HashMap<String, String>();
    private String ubicacionActual = "Refugio";

    private final String[] zonas = {
        "Refugio", "Zona de Árboles Caídos", "Río Contaminado", 
        "Claro de Hongos", "Templo Parcial", "Avión Siniestrado",
        "Cueva Húmeda", "Colina Rocosa", "Pantano Profundo",
        "Bosque Denso", "Cascada Oculta", "Valle de Frutas",
        "Ruinas Antiguas", "Lago Estancado", "Sendero Empinado",
        "Zona de Lianas Colgantes", "Claro de Insectos", "Árbol Gigante",
        "Río Rápido", "Cañón Estrecho", "Pradera Abierta"
    };
    private int zonaIndex = 0;

    private boolean fotoTomadaEnZona = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(0xFF000000);

        scrollView = new ScrollView(this);
        txtMain = new TextView(this);
        txtMain.setTextColor(0xFFFFFFFF);
        txtMain.setTextSize(16);
        txtMain.setPadding(10, 10, 10, 10);
        scrollView.addView(txtMain);

        buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.VERTICAL);

        mainLayout.addView(scrollView, new LinearLayout.LayoutParams(
							   LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f));
        mainLayout.addView(buttonLayout, new LinearLayout.LayoutParams(
							   LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        setContentView(mainLayout);

        inicializarJuego();
        verificarEcoMuerte();
        mostrarEstado();
    }

    private void inicializarJuego() {
        inventario.clear();
        inventario.add("Cámara Dañada");
        inventario.add("Cuchillo Roto");
        inventario.add("Batería Baja");
        inventario.add("Botella Vacía");
        inventario.add("Ropa Rasgada");

        diarioPaginas.clear();
        diarioPaginas.add("Página 1: Motor se calló 14:33. Altitud 600m. Sobrevivió el copiloto.");
        diarioPaginas.add("Página 2: Agua contaminada en el río. Hervir antes de beber.");
        diarioPaginas.add("Página 3: Hongos rojos son venenosos. Evitar.");
        diarioPaginas.add("Página 4: Templo a 3km norte. Paredes de piedra negra. Recursos posibles.");
        diarioPaginas.add("Página 5: Animales nocturnos activos. Mantener fuego.");
        diarioPaginas.add("Página 6: Frutas amarillas comestibles en valle.");
        diarioPaginas.add("Página 7: Huellas de jaguar. Mantener distancia.");
        diarioPaginas.add("Página 8: Tormentas frecuentes. Buscar refugio alto.");
        diarioPaginas.add("Página 9: Insectos portan enfermedades. Usar repelente natural.");
        diarioPaginas.add("Página 10: Cascada tiene agua limpia. Filtrar.");
        diarioPaginas.add("Página 11: Cueva para dormir, pero revisar serpientes.");
        diarioPaginas.add("Página 12: Colina ofrece vista para señal.");
        diarioPaginas.add("Página 13: Pantano peligroso, hundimiento.");
        diarioPaginas.add("Página 14: Bosque denso, fácil perderse.");
        diarioPaginas.add("Página 15: Ruinas tienen herramientas antiguas.");

        mesaCrafting.clear();

        dia = 1;
        tiempoMinutos = 360;
        fotosRestantes = 24;
        integridadCamara = 100;
        claridadVision = 70;
        enCampamento = true;
        tieneRefugio = false;
        modoDesesperacion = false;
        alucinando = false;
        diasSinAntibiotico = 0;
        hambre = 50;
        sed = 50;
        moral = 50;
        temperaturaCorporal = 37;
        tieneFuego = false;
        fotoTomadaEnZona = false;
        heridasInspeccionadasHoy = false;
        recolectadoHoy = false;
        tormentaActiva = false;
        animalCerca = false;
        infeccionActiva = false;

        salud[BRAZO_IZQ] = 80;
        salud[BRAZO_DER] = 100;
        salud[PIERNAS] = 60;
        salud[TORSO] = 100;
        salud[CABEZA] = 100;
        salud[PIES] = 100;
    }

    private void verificarEcoMuerte() {
        File muerteFile = new File(getEcoMuerteDir(), MUERTE_FILE);
        if (muerteFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(muerteFile));
                String linea = reader.readLine();
                reader.close();

                if (linea != null && linea.startsWith("CADAVER: ")) {
                    String[] partes = linea.split("\\|");
                    if (partes.length >= 3) {
                        String ubicacionMuerte = partes[1];
                        String objeto = partes[2];

                        inventario.add(objeto);
                        Toast.makeText(this, "ECO DE MUERTE: Encontraste tu cadáver en " + ubicacionMuerte + 
									   ". Recoges: " + objeto, Toast.LENGTH_LONG).show();
                        Toast.makeText(this, "Diálogo de perturbación: '¿Era yo? ¿Sigo siendo yo?'", 
									   Toast.LENGTH_LONG).show();

                        muerteFile.delete();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File getEcoMuerteDir() {
        File dir = new File(Environment.getExternalStorageDirectory(), 
							"Android/data/com.TSgames.SobreVivencia/eco_muerte");
        dir.mkdirs();
        return dir;
    }

    private void guardarEcoMuerte() {
        try {
            File archivo = new File(getEcoMuerteDir(), MUERTE_FILE);
            FileWriter writer = new FileWriter(archivo);
            writer.write("CADAVER: " + ubicacionActual + "|" + 
						 inventario.get(random.nextInt(inventario.size())) + "|" + 
						 "Fragmento de Craneo\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarEstado() {
        if (diasSinAntibiotico >= 3 && !inventario.contains("Antibiotico Natural")) {
            alucinando = true;
            infeccionActiva = true;
        }

        if (tiempoMinutos >= 1200 && !tieneRefugio && enCampamento) {
            activarModoDesesperacion();
            return;
        }

        if (random.nextInt(100) < 10) {
            tormentaActiva = true;
        } else if (random.nextInt(100) < 5) {
            animalCerca = true;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== SOBREVIVENCIA - DÍA ").append(dia).append(" ===\n");
        sb.append("Tiempo: ").append(formatoHora(tiempoMinutos)).append("\n");
        sb.append("Ubicación: ").append(ubicacionActual).append("\n");
        sb.append("Fotos restantes: ").append(fotosRestantes).append("/24\n");
        sb.append("Integridad cámara: ").append(integridadCamara).append("%\n");
        sb.append("Visión: ").append(claridadVision).append("%\n");
        sb.append("Refugio: ").append(tieneRefugio ? "CONSTRUIDO" : "INEXISTENTE").append("\n");
        sb.append("Fuego: ").append(tieneFuego ? "ENCENDIDO" : "APAGADO").append("\n");
        sb.append("Diario: ").append(diarioPaginas.size()).append("/15 páginas\n");
        sb.append("Hambre: ").append(hambre).append("%\n");
        sb.append("Sed: ").append(sed).append("%\n");
        sb.append("Moral: ").append(moral).append("%\n");
        sb.append("Temperatura: ").append(temperaturaCorporal).append("°C\n");

        if (modoDesesperacion) {
            sb.append("\n!!! MODO DESPERACIÓN ACTIVO !!!\n");
        }
        if (alucinando) {
            sb.append("\n!!! ALUCINANDO - NO CONFIAR EN LOS SENTIDOS !!!\n");
        }
        if (tormentaActiva) {
            sb.append("\n!!! TORMENTA ACTIVA - RIESGO DE HIPOTERMIA Y INUNDACIÓN !!!\n");
        }
        if (animalCerca) {
            sb.append("\n!!! ANIMAL CERCA - RIESGO DE ATAQUE !!!\n");
        }
        if (infeccionActiva) {
            sb.append("\n!!! INFECCIÓN ACTIVA - BUSCA ANTIBIÓTICOS !!!\n");
        }

        sb.append("\n=== HERIDAS ===\n");
        sb.append("Brazo Izq: ").append(salud[BRAZO_IZQ]).append("% ");
        if (salud[BRAZO_IZQ] < 50) sb.append("(CORTADO, Infectado)");
        else if (salud[BRAZO_IZQ] < 80) sb.append("(Herido)");
        sb.append("\n");

        sb.append("Brazo Der: ").append(salud[BRAZO_DER]).append("% ");
        if (salud[BRAZO_DER] < 50) sb.append("(Quebrado, Infectado)");
        else if (salud[BRAZO_DER] < 80) sb.append("(Magullado)");
        sb.append("\n");

        sb.append("Piernas: ").append(salud[PIERNAS]).append("% ");
        if (salud[PIERNAS] < 50) sb.append("(Torcidas, Infectadas)");
        else if (salud[PIERNAS] < 80) sb.append("(Cansadas)");
        sb.append("\n");

        sb.append("Torso: ").append(salud[TORSO]).append("% ");
        if (salud[TORSO] < 50) sb.append("(Golpeado, Interno)");
        else if (salud[TORSO] < 80) sb.append("(Doloroso)");
        sb.append("\n");

        sb.append("Cabeza: ").append(salud[CABEZA]).append("% ");
        if (salud[CABEZA] < 50) sb.append("(Conmoción, Infectada)");
        else if (salud[CABEZA] < 80) sb.append("(Dolor de cabeza)");
        sb.append("\n");

        sb.append("Pies: ").append(salud[PIES]).append("% ");
        if (salud[PIES] < 50) sb.append("(Ampollas, Infectados)");
        else if (salud[PIES] < 80) sb.append("(Cansados)");
        sb.append("\n");

        int maxInventario = (salud[BRAZO_IZQ] < 50 || salud[BRAZO_DER] < 50) ? 4 : 8;
        sb.append("\n=== INVENTARIO (").append(inventario.size()).append("/").append(maxInventario).append(") ===\n");
        for (int i = 0; i < inventario.size(); i++) {
            sb.append("- ").append(inventario.get(i)).append("\n");
        }

        sb.append("\n=== MESA DE CRAFTING ===\n");
        if (mesaCrafting.isEmpty()) {
            sb.append("Vacía. Arrastra objetos aquí para combinar.\n");
        } else {
            for (Map.Entry<String, String> entry : mesaCrafting.entrySet()) {
                sb.append("- ").append(entry.getValue()).append("\n");
            }
        }

        sb.append("\n=== DESCRIPCIÓN ===\n");
        sb.append(getDescripcionZona());

        sb.append("\n=== OPCIONES ===\n");
        if (claridadVision < 50) {
            sb.append("(Tu visión está dañada. Usa la cámara para ver detalles)\n");
        }
        if (hambre > 80) {
            sb.append("(Hambre extrema. Debes comer pronto.)\n");
        }
        if (sed > 80) {
            sb.append("(Sed extrema. Debes beber pronto.)\n");
        }
        if (moral < 20) {
            sb.append("(Moral baja. Riesgo de desesperación.)\n");
        }

        txtMain.setText(sb.toString());
        generarBotonesAccion();
    }

    private String formatoHora(int minutos) {
        int horas = minutos / 60;
        int mins = minutos % 60;
        return String.format("%02d:%02d", horas, mins);
    }

    private String getDescripcionZona() {
        if (modoDesesperacion) {
            return "Noche sin refugio. La oscuridad es absoluta. Solo ves siluetas.\n" +
				"Tu respiración se agita. Cada sonido podría ser el último.\n" +
				"Escuchas ramas quebrándose. NO es el viento.";
        }

        if (alucinando) {
            return "La selva se superpone con recuerdos. Ves a tu familia entre los árboles.\n" +
				"Tu mano tiembla. ¿Es real el líquido en esa hoja?";
        }

        if (tormentaActiva) {
            return "Tormenta furiosa. Lluvia torrencial, truenos ensordecedores.\n" +
				"El suelo se encharca, riesgo de inundación y resbalones.";
        }

        if (animalCerca) {
            return "Sonidos de ramas quebrándose. Un animal grande merodea.\n" +
				"Ojos brillando en la oscuridad. Mantén distancia.";
        }

        if (ubicacionActual.equals("Refugio")) {
            if (tieneRefugio) {
                return "Tu refugio improvisado. Techo de hojas, paredes de barro.\n" +
					"Aquí estás (relativamente) a salvo de la selva.";
            } else {
                return "Campamento base. Solo tierra y cenizas del fuego de anoche.\n" +
					"NECESITAS CONSTRUIR UN REFUGIO ANTES DE LA NOCHE (20:00).";
            }
        }

        if (ubicacionActual.equals("Zona de Árboles Caídos")) {
            String desc = "Troncos apilados formando un laberinto natural.\n";
            if (claridadVision >= 50) {
                desc += "Ves hongos en la madera podrida. Insectos bajo la corteza.\n";
                desc += "Posibles recursos: palos, lianas, escarabajos.";
            } else {
                desc += "Todo está borroso. No puedes distinguir detalles.\n";
            }
            return desc;
        }

        if (ubicacionActual.equals("Río Contaminado")) {
            return "Agua turbia fluyendo lentamente. Posible fuente de agua, pero contaminada.\n" +
				"Peces muertos flotando. Riesgo de enfermedades si bebes directo.";
        }

        if (ubicacionActual.equals("Claro de Hongos")) {
            return "Suelo cubierto de hongos multicolores. Algunos comestibles, otros venenosos.\n" +
				"Aire húmedo y esporas flotando. No respirar profundo.";
        }

        if (ubicacionActual.equals("Templo Parcial")) {
            return "Ruinas de piedra cubiertas de enredaderas. Posibles artefactos antiguos.\n" +
				"Estructuras inestables, riesgo de colapso.";
        }

        if (ubicacionActual.equals("Avión Siniestrado")) {
            return "Restos del avión esparcidos. Posibles suministros restantes.\n" +
				"Metal retorcido, olor a combustible. Cuidado con cortes.";
        }

        if (ubicacionActual.equals("Cueva Húmeda")) {
            return "Entrada oscura y húmeda. Posible refugio temporal.\n" +
				"Murciélagos y posibles serpientes dentro.";
        }

        if (ubicacionActual.equals("Colina Rocosa")) {
            return "Terreno empinado con rocas sueltas. Buena vista panorámica.\n" +
				"Riesgo de caídas. Posibles minerales o hierbas.";
        }

        if (ubicacionActual.equals("Pantano Profundo")) {
            return "Agua estancada y barro pegajoso. Fácil hundirse.\n" +
				"Plantas acuáticas y mosquitos por doquier.";
        }

        if (ubicacionActual.equals("Bosque Denso")) {
            return "Árboles altos bloqueando la luz. Fácil desorientarse.\n" +
				"Sonidos de aves y monos. Recursos abundantes pero ocultos.";
        }

        if (ubicacionActual.equals("Cascada Oculta")) {
            return "Agua cayendo con fuerza. Fuente de agua limpia.\n" +
				"Rocas resbaladizas alrededor. Posible cueva detrás.";
        }

        if (ubicacionActual.equals("Valle de Frutas")) {
            return "Árboles frutales dispersos. Frutas comestibles disponibles.\n" +
				"Atrae animales. Cuidado con frutas podridas.";
        }

        if (ubicacionActual.equals("Ruinas Antiguas")) {
            return "Estructuras derruidas cubiertas de musgo. Historia olvidada.\n" +
				"Posibles trampas antiguas o tesoros.";
        }

        if (ubicacionActual.equals("Lago Estancado")) {
            return "Agua quieta con algas. No beber sin filtrar.\n" +
				"Peces y ranas. Posible pesca.";
        }

        if (ubicacionActual.equals("Sendero Empinado")) {
            return "Camino estrecho subiendo. Fatigante pero con vistas.\n" +
				"Riesgo de desprendimientos de rocas.";
        }

        if (ubicacionActual.equals("Zona de Lianas Colgantes")) {
            return "Lianas colgando de árboles altos. Útiles para escalar o atar.\n" +
				"Monos saltando arriba. Posible caída.";
        }

        if (ubicacionActual.equals("Claro de Insectos")) {
            return "Enjambre de insectos zumbando. Fuente de proteína pero riesgoso.\n" +
				"Picaduras posibles. Usar fuego para ahuyentar.";
        }

        if (ubicacionActual.equals("Árbol Gigante")) {
            return "Árbol masivo con raíces expuestas. Posible refugio bajo raíces.\n" +
				"Corteza útil para herramientas.";
        }

        if (ubicacionActual.equals("Río Rápido")) {
            return "Corriente fuerte. Difícil cruzar. Peces rápidos.\n" +
				"Riesgo de ahogamiento.";
        }

        if (ubicacionActual.equals("Cañón Estrecho")) {
            return "Paredes altas de roca. Eco de sonidos. Posible escalada.\n" +
				"Riesgo de inundación repentina.";
        }

        if (ubicacionActual.equals("Pradera Abierta")) {
            return "Espacio abierto con hierba alta. Buena para señales.\n" +
				"Expuesto a elementos y animales.";
        }

        return "Zona desconocida de la selva. Cada paso es un riesgo calculado.\n" +
			"El barro fresco guarda secretos. La luz del día se filtra débilmente.";
    }

    private void generarBotonesAccion() {
        buttonLayout.removeAllViews();

        if (modoDesesperacion) {
            addButton("MORIR (Sin refugio)", new View.OnClickListener() {
					public void onClick(View v) {
						ejecutarMuerte("La noche sin refugio te consume. Hipotermia y desangrado.");
					}
				});
            return;
        }

        if (enCampamento) {
            addButton("Inspeccionar Heridas (15min)", new View.OnClickListener() {
					public void onClick(View v) {
						inspeccionarHeridas();
					}
				});

            addButton("Tomar Foto (Campamento) (1foto)", new View.OnClickListener() {
					public void onClick(View v) {
						tomarFotoCampamento();
					}
				});

            addButton("Construir Refugio (2h)", new View.OnClickListener() {
					public void onClick(View v) {
						construirRefugio();
					}
				});

            addButton("Encender Fuego (30min)", new View.OnClickListener() {
					public void onClick(View v) {
						encenderFuego();
					}
				});

            addButton("Cocinar Comida (45min)", new View.OnClickListener() {
					public void onClick(View v) {
						cocinarComida();
					}
				});

            addButton("Purificar Agua (30min)", new View.OnClickListener() {
					public void onClick(View v) {
						purificarAgua();
					}
				});

            addButton("Comer (15min)", new View.OnClickListener() {
					public void onClick(View v) {
						comer();
					}
				});

            addButton("Beber (10min)", new View.OnClickListener() {
					public void onClick(View v) {
						beber();
					}
				});

            addButton("Descansar (1h)", new View.OnClickListener() {
					public void onClick(View v) {
						descansar();
					}
				});

            addButton("Viajar a Zona (4h)", new View.OnClickListener() {
					public void onClick(View v) {
						viajarAZona();
					}
				});

            addButton("Administrar Inventario", new View.OnClickListener() {
					public void onClick(View v) {
						administrarInventario();
					}
				});

            addButton("Leer Diario", new View.OnClickListener() {
					public void onClick(View v) {
						leerDiario();
					}
				});

            if (tiempoMinutos >= 1320) {
                addButton("DORMIR (Avanzar día)", new View.OnClickListener() {
						public void onClick(View v) {
							avanzarDia();
						}
					});
            }
        } else {
            addButton("Inspeccionar Entorno (15min)", new View.OnClickListener() {
					public void onClick(View v) {
						inspeccionarEntorno();
					}
				});

            addButton("Recolectar Recursos (30min-2h)", new View.OnClickListener() {
					public void onClick(View v) {
						recolectarRecursos();
					}
				});

            addButton("Cazar Animal (1h)", new View.OnClickListener() {
					public void onClick(View v) {
						cazarAnimal();
					}
				});

            addButton("Pescar (45min)", new View.OnClickListener() {
					public void onClick(View v) {
						pescar();
					}
				});

            addButton("Recolectar Agua (20min)", new View.OnClickListener() {
					public void onClick(View v) {
						recolectarAgua();
					}
				});

            addButton("Tomar Foto (Zona) (1foto)", new View.OnClickListener() {
					public void onClick(View v) {
						tomarFotoZona();
					}
				});

            addButton("Colocar Trampa (30min)", new View.OnClickListener() {
					public void onClick(View v) {
						colocarTrampa();
					}
				});

            addButton("Explorar Profundo (2h)", new View.OnClickListener() {
					public void onClick(View v) {
						explorarProfundo();
					}
				});

            addButton("Escalar (1h)", new View.OnClickListener() {
					public void onClick(View v) {
						escalar();
					}
				});

            addButton("Regresar Campamento (4h)", new View.OnClickListener() {
					public void onClick(View v) {
						regresarCampamento();
					}
				});
        }

        if (integridadCamara <= 0) {
            Toast.makeText(this, "SONIDO: Cámara rota. Clicks mecánicos vacíos.", 
						   Toast.LENGTH_SHORT).show();
        }
    }

    private void addButton(String texto, View.OnClickListener listener) {
        Button btn = new Button(this);
        btn.setText(texto);
        btn.setOnClickListener(listener);
        buttonLayout.addView(btn);
    }

    private void inspeccionarHeridas() {
        if (!enCampamento) {
            Toast.makeText(this, "Debes estar en el campamento.", Toast.LENGTH_SHORT).show();
            return;
        }

        tiempoMinutos += 15;
        heridasInspeccionadasHoy = true;

        for (int i = 0; i < salud.length; i++) {
            if (salud[i] < 80) {
                salud[i] = Math.min(100, salud[i] + 5 + random.nextInt(6));
            }
        }

        if (infeccionActiva && inventario.contains("Antibiotico Natural")) {
            infeccionActiva = false;
            diasSinAntibiotico = 0;
            alucinando = false;
            Toast.makeText(this, "Infección tratada con antibiótico natural.", Toast.LENGTH_SHORT).show();
            inventario.remove("Antibiotico Natural");
        }

        Toast.makeText(this, "INSPECCIÓN: Heridas limpiadas. +5-10% salud en áreas afectadas.", Toast.LENGTH_SHORT).show();
        mostrarEstado();
    }

    private void tomarFotoCampamento() {
        if (fotosRestantes <= 0) {
            Toast.makeText(this, "FLASH: Batería agotada.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (integridadCamara <= 0) {
            Toast.makeText(this, "SONIDO: Cámara rota. Clicks mecánicos vacíos.", Toast.LENGTH_SHORT).show();
            return;
        }

        fotosRestantes--;
        integridadCamara = Math.max(0, integridadCamara - 5);
        claridadVision = Math.min(100, claridadVision + 5);

        if (animalCerca) {
            animalCerca = false;
            Toast.makeText(this, "FOTO: Animal ahuyentado por flash.", 
						   Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "FOTO: Campamento documentado. Nada anormal.", 
						   Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "INTEGRIDAD CÁMARA: -5%", Toast.LENGTH_SHORT).show();
        mostrarEstado();
    }

    private void construirRefugio() {
        if (!enCampamento) {
            Toast.makeText(this, "Debes estar en el campamento.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tieneRefugio) {
            Toast.makeText(this, "Refugio ya construido.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean tienePalo = inventario.contains("Palo Robusto");
        boolean tieneLianas = inventario.contains("Lianas");
        boolean tieneHojas = inventario.contains("Hojas Grandes");
        boolean tieneBarro = inventario.contains("Barro Pegajoso");

        if (tienePalo && tieneLianas && tieneHojas && tieneBarro) {
            tiempoMinutos += 120;
            tieneRefugio = true;
            inventario.remove("Palo Robusto");
            inventario.remove("Lianas");
            inventario.remove("Hojas Grandes");
            inventario.remove("Barro Pegajoso");
            Toast.makeText(this, "REFUGIO CONSTRUIDO. Te salvaste de la noche.", 
						   Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "FALTAN: Palo Robusto, Lianas, Hojas Grandes, Barro Pegajoso", 
						   Toast.LENGTH_SHORT).show();
        }

        mostrarEstado();
    }

    private void encenderFuego() {
        if (!enCampamento) {
            Toast.makeText(this, "Debes estar en el campamento.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (tieneFuego) {
            Toast.makeText(this, "Fuego ya encendido.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean tieneLeña = inventario.contains("Leña Seca");
        boolean tienePiedra = inventario.contains("Piedra Chispeante");

        if (tieneLeña && tienePiedra) {
            tiempoMinutos += 30;
            tieneFuego = true;
            inventario.remove("Leña Seca");
            inventario.remove("Piedra Chispeante");
            temperaturaCorporal += 2;
            moral += 10;
            Toast.makeText(this, "FUEGO ENCENDIDO. Calor y luz. +2°C temperatura, +10 moral.", 
						   Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "FALTAN: Leña Seca, Piedra Chispeante", 
						   Toast.LENGTH_SHORT).show();
        }

        mostrarEstado();
    }

    private void cocinarComida() {
        if (!enCampamento || !tieneFuego) {
            Toast.makeText(this, "Necesitas estar en campamento con fuego encendido.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean tieneCarne = inventario.contains("Carne Cruda") || inventario.contains("Pez Crudo");
        boolean tieneHongos = inventario.contains("Hongos Comestibles");

        if (tieneCarne || tieneHongos) {
            tiempoMinutos += 45;
            if (inventario.contains("Carne Cruda")) {
                inventario.remove("Carne Cruda");
                inventario.add("Carne Cocida");
            } else if (inventario.contains("Pez Crudo")) {
                inventario.remove("Pez Crudo");
                inventario.add("Pez Cocido");
            } else {
                inventario.remove("Hongos Comestibles");
                inventario.add("Hongos Cocidos");
            }
            Toast.makeText(this, "COMIDA COCINADA. Segura para comer.", 
						   Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "FALTAN: Carne Cruda, Pez Crudo o Hongos Comestibles", 
						   Toast.LENGTH_SHORT).show();
        }

        mostrarEstado();
    }

    private void purificarAgua() {
        if (!enCampamento || !tieneFuego) {
            Toast.makeText(this, "Necesitas estar en campamento con fuego encendido.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (inventario.contains("Agua Contaminada") && inventario.contains("Botella Vacía")) {
            tiempoMinutos += 30;
            inventario.remove("Agua Contaminada");
            inventario.add("Agua Purificada");
            Toast.makeText(this, "AGUA PURIFICADA. Segura para beber.", 
						   Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "FALTAN: Agua Contaminada y Botella Vacía", 
						   Toast.LENGTH_SHORT).show();
        }

        mostrarEstado();
    }

    private void comer() {
        if (inventario.contains("Fruta Comestible") || inventario.contains("Carne Cocida") || inventario.contains("Pez Cocido") || inventario.contains("Hongos Cocidos")) {
            tiempoMinutos += 15;
            hambre = Math.max(0, hambre - 30 - random.nextInt(21));
            moral += 5;
            String comida = "";
            if (inventario.contains("Carne Cocida")) {
                comida = "Carne Cocida";
            } else if (inventario.contains("Pez Cocido")) {
                comida = "Pez Cocido";
            } else if (inventario.contains("Hongos Cocidos")) {
                comida = "Hongos Cocidos";
            } else {
                comida = "Fruta Comestible";
            }
            inventario.remove(comida);
            Toast.makeText(this, "COMISTE " + comida + ". -30-50 hambre, +5 moral.", 
						   Toast.LENGTH_SHORT).show();
            if (alucinando && random.nextInt(100) < 20) {
                Toast.makeText(this, "ALUCINACIÓN: Comiste algo venenoso por error. +20 hambre.", 
							   Toast.LENGTH_SHORT).show();
                hambre += 20;
            }
        } else {
            Toast.makeText(this, "No tienes comida comestible.", Toast.LENGTH_SHORT).show();
        }

        mostrarEstado();
    }

    private void beber() {
        if (inventario.contains("Agua Purificada")) {
            tiempoMinutos += 10;
            sed = Math.max(0, sed - 30 - random.nextInt(21));
            inventario.remove("Agua Purificada");
            inventario.add("Botella Vacía");
            Toast.makeText(this, "BEBISTE Agua Purificada. -30-50 sed.", 
						   Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No tienes agua purificada.", Toast.LENGTH_SHORT).show();
        }

        mostrarEstado();
    }

    private void descansar() {
        tiempoMinutos += 60;
        moral = Math.min(100, moral + 10 + random.nextInt(11));
        claridadVision = Math.min(100, claridadVision + 5);
        for (int i = 0; i < salud.length; i++) {
            salud[i] = Math.min(100, salud[i] + 2);
        }
        Toast.makeText(this, "DESCANSASTE. +10-20 moral, +5 visión, +2 salud general.", 
					   Toast.LENGTH_SHORT).show();
        mostrarEstado();
    }

    private void viajarAZona() {
        if (!enCampamento) {
            Toast.makeText(this, "Ya estás en una zona.", Toast.LENGTH_SHORT).show();
            return;
        }

        enCampamento = false;
        tiempoMinutos += 240;
        zonaIndex = (zonaIndex + 1) % zonas.length;
        ubicacionActual = zonas[zonaIndex];
        fotoTomadaEnZona = false;
        recolectadoHoy = false;

        hambre += 10;
        sed += 10;

        if (random.nextInt(100) < 20) {
            animalCerca = true;
            Toast.makeText(this, "AUDIO: Rugido lejano. Animal cerca.", 
						   Toast.LENGTH_SHORT).show();
        }

        if (random.nextInt(100) < 15) {
            tormentaActiva = true;
            Toast.makeText(this, "Tormenta inicia durante el viaje. -5 temperatura.", 
						   Toast.LENGTH_SHORT).show();
            temperaturaCorporal -= 5;
        }

        if (random.nextInt(100) < 10) {
            salud[PIERNAS] -= 10;
            Toast.makeText(this, "Tropezón en raíces. -10% piernas.", 
						   Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "VIAJE: 4 horas perdidas. Llegaste a " + ubicacionActual, 
					   Toast.LENGTH_SHORT).show();
        mostrarEstado();
    }

    private void regresarCampamento() {
        if (enCampamento) {
            Toast.makeText(this, "Ya estás en el campamento.", Toast.LENGTH_SHORT).show();
            return;
        }

        enCampamento = true;
        tiempoMinutos += 240;
        ubicacionActual = "Refugio";
        animalCerca = false;
        tormentaActiva = false;

        hambre += 10;
        sed += 10;

        if (random.nextInt(100) < 10) {
            salud[PIES] -= 10;
            Toast.makeText(this, "Ampollas en pies por caminata. -10% pies.", 
						   Toast.LENGTH_SHORT).show();
        }

        Toast.makeText(this, "REGRESO: 4 horas perdidas. De vuelta al campamento.", 
					   Toast.LENGTH_SHORT).show();
        mostrarEstado();
    }

    private void inspeccionarEntorno() {
        if (enCampamento) {
            Toast.makeText(this, "Ve a una zona para inspeccionar.", Toast.LENGTH_SHORT).show();
            return;
        }

        tiempoMinutos += 15;

        if (claridadVision < 50) {
            Toast.makeText(this, "VISIÓN DAÑADA: Solo ves sombras. Usa la cámara.", 
						   Toast.LENGTH_SHORT).show();
        } else {
            if (animalCerca) {
                Toast.makeText(this, "INSPECCIÓN: Huellas de animal. Jaguar o mono cercano.", 
							   Toast.LENGTH_SHORT).show();
            } else if (tormentaActiva) {
                Toast.makeText(this, "INSPECCIÓN: Nubes oscuras. Tormenta intensificándose.", 
							   Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "INSPECCIÓN: Rastros de recursos. Frutas, agua, plantas útiles.", 
							   Toast.LENGTH_SHORT).show();
                if (random.nextInt(100) < 20) {
                    inventario.add("Hoja Medicinal");
                    Toast.makeText(this, "Encontraste Hoja Medicinal.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        mostrarEstado();
    }

    private void recolectarRecursos() {
        if (enCampamento) {
            Toast.makeText(this, "Ve a una zona para recolectar.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (recolectadoHoy) {
            Toast.makeText(this, "Ya recolectaste hoy en esta zona. Recursos escasos.", Toast.LENGTH_SHORT).show();
            return;
        }

        int tiempo = 30 + random.nextInt(90);
        tiempoMinutos += tiempo;

        int maxInventario = (salud[BRAZO_IZQ] < 50 || salud[BRAZO_DER] < 50) ? 4 : 8;
        if (inventario.size() >= maxInventario) {
            Toast.makeText(this, "INVENTARIO LLENO. Objeto caído al barro. PERDIDO.", 
						   Toast.LENGTH_SHORT).show();
            return;
        }

        String[] recursosBase = {"Palo Robusto", "Lianas", "Hojas Grandes", "Hongos Comestibles", "Escarabajos", "Fruta Comestible", "Leña Seca", "Piedra Chispeante", "Barro Pegajoso", "Hoja Medicinal", "Corteza Útil", "Semillas", "Raíces Comestibles", "Piedras Afiladas", "Enredaderas", "Musgo Absorbente", "Flores Repelentes", "Huevos de Ave", "Miel", "Arcilla"};
        String recurso = recursosBase[random.nextInt(recursosBase.length)];

        if (alucinando && random.nextInt(100) < 50) {
            Toast.makeText(this, "ALUCINACIÓN: Recolectaste " + recurso + " (¡ES FALSO!)", 
						   Toast.LENGTH_LONG).show();
            diasSinAntibiotico++;
        } else {
            if (!heridasInspeccionadasHoy && random.nextInt(100) < 30) {
                Toast.makeText(this, "PIQUETE: Insecto o espina no vista. ERROR: No inspeccionaste.", 
							   Toast.LENGTH_SHORT).show();
                salud[random.nextInt(salud.length)] -= 15;
            }

            if (tormentaActiva && random.nextInt(100) < 40) {
                Toast.makeText(this, "TORMENTA: Resbalón en barro. -10% piernas.", 
							   Toast.LENGTH_SHORT).show();
                salud[PIERNAS] -= 10;
            }

            inventario.add(recurso);
            recolectadoHoy = true;
            Toast.makeText(this, "RECOLECTADO: " + recurso + " (+" + tiempo + "min)", 
						   Toast.LENGTH_SHORT).show();
        }

        mostrarEstado();
    }

    private void cazarAnimal() {
        if (enCampamento) {
            Toast.makeText(this, "Ve a una zona para cazar.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!inventario.contains("Lanza Primitiva") && !inventario.contains("Trampa Colocada")) {
            Toast.makeText(this, "Necesitas Lanza Primitiva o Trampa.", Toast.LENGTH_SHORT).show();
            return;
        }

        tiempoMinutos += 60;

        if (animalCerca || random.nextInt(100) < 40) {
            inventario.add("Carne Cruda");
            Toast.makeText(this, "CAZA EXITOSA: Carne Cruda obtenida.", 
						   Toast.LENGTH_LONG).show();
            animalCerca = false;
        } else {
            salud[TORSO] -= 20;
            Toast.makeText(this, "CAZA FALLIDA: Animal te ataca. -20% torso.", 
						   Toast.LENGTH_LONG).show();
        }

        mostrarEstado();
    }

    private void pescar() {
        if (enCampamento || (!ubicacionActual.contains("Río") && !ubicacionActual.contains("Lago") && !ubicacionActual.contains("Cascada"))) {
            Toast.makeText(this, "Necesitas estar cerca de agua.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!inventario.contains("Palo Afilado")) {
            Toast.makeText(this, "Necesitas Palo Afilado para pescar.", Toast.LENGTH_SHORT).show();
            return;
        }

        tiempoMinutos += 45;

        if (random.nextInt(100) < 50) {
            inventario.add("Pez Crudo");
            Toast.makeText(this, "PESCA EXITOSA: Pez Crudo obtenido.", 
						   Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "PESCA FALLIDA: Nada mordió.", 
						   Toast.LENGTH_SHORT).show();
        }

        mostrarEstado();
    }

    private void recolectarAgua() {
        if (enCampamento || (!ubicacionActual.contains("Río") && !ubicacionActual.contains("Lago") && !ubicacionActual.contains("Cascada"))) {
            Toast.makeText(this, "Necesitas estar cerca de agua.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!inventario.contains("Botella Vacía")) {
            Toast.makeText(this, "Necesitas Botella Vacía.", Toast.LENGTH_SHORT).show();
            return;
        }

        tiempoMinutos += 20;
        inventario.remove("Botella Vacía");
        inventario.add("Agua Contaminada");
        Toast.makeText(this, "AGUA RECOLECTADA: Contaminada, purifícala.", 
					   Toast.LENGTH_SHORT).show();

        if (random.nextInt(100) < 20) {
            sed += 20;
            Toast.makeText(this, "Accidente: Bebiste un sorbo contaminado por error. +20 sed.", 
						   Toast.LENGTH_SHORT).show();
        }

        mostrarEstado();
    }

    private void tomarFotoZona() {
        if (fotosRestantes <= 0) {
            Toast.makeText(this, "FLASH: Batería agotada.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (integridadCamara <= 0) {
            Toast.makeText(this, "SONIDO: Cámara rota. Clicks mecánicos vacíos.", Toast.LENGTH_SHORT).show();
            return;
        }

        fotosRestantes--;
        integridadCamara = Math.max(0, integridadCamara - 5);
        fotoTomadaEnZona = true;
        claridadVision = Math.min(100, claridadVision + 10);

        if (animalCerca) {
            animalCerca = false;
            Toast.makeText(this, "FOTO: Animal ahuyentado por flash.", 
						   Toast.LENGTH_LONG).show();
        } else if (tormentaActiva) {
            Toast.makeText(this, "FOTO: Relámpago capturado. +10 claridad temporal.", 
						   Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "FOTO: Detalles ocultos revelados. +10% claridad.", 
						   Toast.LENGTH_SHORT).show();
            if (random.nextInt(100) < 30) {
                diarioPaginas.add("Página Extra: Foto revela ruta segura en " + ubicacionActual + ".");
                Toast.makeText(this, "Foto revela nueva página de diario.", Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(this, "INTEGRIDAD CÁMARA: -5%", Toast.LENGTH_SHORT).show();
        mostrarEstado();
    }

    private void colocarTrampa() {
        if (!inventario.contains("Lianas") || !inventario.contains("Palo Robusto") || !inventario.contains("Piedras Afiladas")) {
            Toast.makeText(this, "NECESITAS: Lianas + Palo Robusto + Piedras Afiladas", Toast.LENGTH_SHORT).show();
            return;
        }

        tiempoMinutos += 30;
        inventario.add("Trampa Colocada");
        inventario.remove("Lianas");
        inventario.remove("Palo Robusto");
        inventario.remove("Piedras Afiladas");

        if (animalCerca) {
            inventario.add("Carne Cruda");
            Toast.makeText(this, "TRAMPA COLOCADA: Animal cae. Carne Cruda obtenida.", 
						   Toast.LENGTH_LONG).show();
            animalCerca = false;
        } else {
            Toast.makeText(this, "TRAMPA COLOCADA: Esperando presa.", 
						   Toast.LENGTH_SHORT).show();
        }

        mostrarEstado();
    }

    private void explorarProfundo() {
        if (enCampamento) {
            Toast.makeText(this, "Ve a una zona para explorar profundo.", Toast.LENGTH_SHORT).show();
            return;
        }

        tiempoMinutos += 120;
        hambre += 15;
        sed += 15;

        if (random.nextInt(100) < 40) {
            String[] tesoros = {"Artefacto Antiguo", "Herramienta Oxidada", "Mapa Rasgado", "Botiquín Viejo", "Batería Extra"};
            String tesoro = tesoros[random.nextInt(tesoros.length)];
            inventario.add(tesoro);
            Toast.makeText(this, "EXPLORACIÓN: Encontraste " + tesoro + ".", 
						   Toast.LENGTH_LONG).show();
            if (tesoro.equals("Botiquín Viejo")) {
                for (int i = 0; i < salud.length; i++) {
                    salud[i] = Math.min(100, salud[i] + 20);
                }
            }
        } else if (random.nextInt(100) < 30) {
            salud[random.nextInt(salud.length)] -= 25;
            Toast.makeText(this, "EXPLORACIÓN FALLIDA: Caída o picadura. -25% en herida aleatoria.", 
						   Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "EXPLORACIÓN: Nada nuevo encontrado.", 
						   Toast.LENGTH_SHORT).show();
        }

        mostrarEstado();
    }

    private void escalar() {
        if (enCampamento || (!ubicacionActual.contains("Colina") && !ubicacionActual.contains("Cañón") && !ubicacionActual.contains("Sendero") && !ubicacionActual.contains("Árbol"))) {
            Toast.makeText(this, "Necesitas terreno para escalar.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!inventario.contains("Lianas") && !inventario.contains("Enredaderas")) {
            Toast.makeText(this, "Necesitas Lianas o Enredaderas para escalar seguro.", Toast.LENGTH_SHORT).show();
            return;
        }

        tiempoMinutos += 60;
        hambre += 10;
        sed += 10;

        if (random.nextInt(100) < 60) {
            Toast.makeText(this, "ESCALADA EXITOSA: Mejor vista, +10 moral.", 
						   Toast.LENGTH_SHORT).show();
            moral += 10;
            if (random.nextInt(100) < 30) {
                inventario.add("Fruta Comestible");
                Toast.makeText(this, "Encontraste Fruta Comestible arriba.", Toast.LENGTH_SHORT).show();
            }
        } else {
            salud[PIERNAS] -= 20;
            salud[BRAZO_IZQ] -= 10;
            Toast.makeText(this, "ESCALADA FALLIDA: Resbalón. -20% piernas, -10% brazo izq.", 
						   Toast.LENGTH_LONG).show();
        }

        mostrarEstado();
    }

    private void activarModoDesesperacion() {
        modoDesesperacion = true;
        mostrarEstado();
    }

    private void administrarInventario() {
        buttonLayout.removeAllViews();

        txtMain.setText("=== INVENTARIO ===\n" + 
						"Arrastra objetos a la mesa de crafting:\n");

        for (int i = 0; i < inventario.size(); i++) {
            final String objeto = inventario.get(i);
            addButton("→ " + objeto, new View.OnClickListener() {
					public void onClick(View v) {
						mesaCrafting.put(objeto, objeto);
						Toast.makeText(MainActivity.this, "Objeto en mesa: " + objeto, 
									   Toast.LENGTH_SHORT).show();
						mostrarEstado();
					}
				});
        }

        addButton("COMBINAR OBJETOS", new View.OnClickListener() {
				public void onClick(View v) {
					combinarCrafting();
				}
			});

        addButton("LIMPIAR MESA", new View.OnClickListener() {
				public void onClick(View v) {
					mesaCrafting.clear();
					Toast.makeText(MainActivity.this, "Mesa limpiada.", Toast.LENGTH_SHORT).show();
					mostrarEstado();
				}
			});

        addButton("VOLVER", new View.OnClickListener() {
				public void onClick(View v) {
					mostrarEstado();
				}
			});
    }

    private void combinarCrafting() {
        if (mesaCrafting.size() < 2) {
            Toast.makeText(this, "NECESITAS mínimo 2 objetos.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean tienePalo = mesaCrafting.containsKey("Palo Robusto");
        boolean tieneLianas = mesaCrafting.containsKey("Lianas");
        boolean tieneCuchillo = mesaCrafting.containsKey("Cuchillo Roto");
        boolean tieneHongos = mesaCrafting.containsKey("Hongos Comestibles");
        boolean tieneEscarabajos = mesaCrafting.containsKey("Escarabajos");
        boolean tienePiedra = mesaCrafting.containsKey("Piedra Chispeante");
        boolean tieneCorteza = mesaCrafting.containsKey("Corteza Útil");
        boolean tieneMusgo = mesaCrafting.containsKey("Musgo Absorbente");
        boolean tieneFlores = mesaCrafting.containsKey("Flores Repelentes");
        boolean tienePiedrasAfiladas = mesaCrafting.containsKey("Piedras Afiladas");
        boolean tieneHojaMedicinal = mesaCrafting.containsKey("Hoja Medicinal");
        boolean tieneArcilla = mesaCrafting.containsKey("Arcilla");
        boolean tieneSemillas = mesaCrafting.containsKey("Semillas");
        boolean tieneRaices = mesaCrafting.containsKey("Raíces Comestibles");
		boolean tieneHojasGrandes = mesaCrafting.containsKey("Hojas Grandes");
		boolean tieneBarroPegajoso = mesaCrafting.containsKey("Barro Pegajoso");
        boolean tieneEnredaderas = mesaCrafting.containsKey("Enredaderas");

        String resultado = "";
        boolean exito = false;

        if (tienePalo && tieneLianas) {
            resultado = "Lanza Primitiva";
            exito = true;
        } else if (tieneHongos && tieneEscarabajos && tieneHojaMedicinal) {
            resultado = "Antibiotico Natural";
            exito = true;
            diasSinAntibiotico = 0;
            alucinando = false;
            infeccionActiva = false;
        } else if (tieneCuchillo && tienePalo && tienePiedrasAfiladas) {
            resultado = "Palo Afilado";
            exito = true;
        } else if (tienePiedra && tieneCorteza) {
            resultado = "Herramienta de Fuego";
            exito = true;
        } else if (tieneMusgo && tieneFlores) {
            resultado = "Repelente Natural";
            exito = true;
        } else if (tieneArcilla && tieneSemillas) {
            resultado = "Maceta Primitiva";
            exito = true;
        } else if (tieneRaices && tieneEnredaderas) {
            resultado = "Cuerda Fuerte";
            exito = true;
        } else if (tieneLianas && tieneHojasGrandes && tieneBarroPegajoso) {
            resultado = "Red de Pesca";
            exito = true;
        } else if (tienePalo && tieneCuchillo && tieneCorteza) {
            resultado = "Arco Primitivo";
            exito = true;
        } else if (tienePiedrasAfiladas && tienePalo) {
            resultado = "Hacha Improvisada";
            exito = true;
        }

        if (exito) {
            inventario.add(resultado);
            Toast.makeText(this, "¡CRAFTING EXITOSO! Creaste: " + resultado, 
						   Toast.LENGTH_LONG).show();
            Toast.makeText(this, "AUDIO: Sonido de corte y amarre.", 
						   Toast.LENGTH_SHORT).show();
            for (String key : mesaCrafting.keySet()) {
                inventario.remove(key);
            }
        } else {
            String objetoPerdido = mesaCrafting.keySet().iterator().next();
            Toast.makeText(this, "CRAFTING FALLIDO. PERDISTE: " + objetoPerdido, 
						   Toast.LENGTH_LONG).show();
            Toast.makeText(this, "SONIDO: Error permanente. Materiales desintegrados.", 
						   Toast.LENGTH_SHORT).show();
            inventario.remove(objetoPerdido);
        }

        mesaCrafting.clear();
        mostrarEstado();
    }

    private void leerDiario() {
        StringBuilder sb = new StringBuilder("=== DIARIO DEL PILOTO ===\n");
        for (int i = 0; i < diarioPaginas.size(); i++) {
            sb.append(diarioPaginas.get(i)).append("\n\n");
        }

        if (diarioPaginas.size() >= 10) {
            sb.append("\n=== CONOCIMIENTOS AVANZADOS ===\n");
            sb.append("Combinaciones: Palo + Lianas = Lanza.\n");
            sb.append("Hongos + Escarabajos + Hoja = Antibiótico.\n");
            sb.append("Supervivencia: Mantén hambre/sed <50, moral >30.\n");
            sb.append("Zonas clave: Valle para frutas, Río para pesca.\n");
            sb.append("EXIF: 'Altitud 600m, motor falló 14:33'");
        }

        txtMain.setText(sb.toString());

        buttonLayout.removeAllViews();
        addButton("VOLVER", new View.OnClickListener() {
				public void onClick(View v) {
					mostrarEstado();
				}
			});
    }

    private void avanzarDia() {
        dia++;
        tiempoMinutos = 360;
        fotosRestantes = 24;
        heridasInspeccionadasHoy = false;
        recolectadoHoy = false;
        tormentaActiva = false;
        animalCerca = false;
        diasSinAntibiotico++;
        hambre += 20 + random.nextInt(11);
        sed += 20 + random.nextInt(11);
        moral -= 5 + random.nextInt(6);
        if (!tieneFuego) {
            temperaturaCorporal -= 2;
        } else {
            tieneFuego = false;
        }

        for (int i = 0; i < salud.length; i++) {
            if (salud[i] < 50) {
                salud[i] = Math.max(0, salud[i] - 5 - random.nextInt(6));
            }
        }

        if (hambre >= 100) {
            ejecutarMuerte("Muerte por inanición. El hambre te debilitó demasiado.");
            return;
        }

        if (sed >= 100) {
            ejecutarMuerte("Muerte por deshidratación. La sed te consumió.");
            return;
        }

        if (moral <= 0) {
            ejecutarMuerte("Muerte por desesperación. Perdiste la voluntad de vivir.");
            return;
        }

        if (temperaturaCorporal <= 32) {
            ejecutarMuerte("Muerte por hipotermia. El frío de la noche te mató.");
            return;
        } else if (temperaturaCorporal >= 42) {
            ejecutarMuerte("Muerte por hipertermia. El calor y fiebre te abatieron.");
            return;
        }

        if (infeccionActiva) {
            temperaturaCorporal += 1;
            for (int i = 0; i < salud.length; i++) {
                salud[i] -= 5;
            }
        }

        for (int i = 0; i < salud.length; i++) {
            if (salud[i] <= 0) {
                ejecutarMuerte("Herida mortal en " + getNombreHerida(i));
                return;
            }
        }

        if (dia > 60) {
            ejecutarVictoria();
        } else {
            Toast.makeText(this, "=== DÍA " + dia + " ===", Toast.LENGTH_LONG).show();
            mostrarEstado();
        }
    }

    private String getNombreHerida(int index) {
        switch (index) {
            case BRAZO_IZQ: return "Brazo Izquierdo";
            case BRAZO_DER: return "Brazo Derecho";
            case PIERNAS: return "Piernas";
            case TORSO: return "Torso";
            case CABEZA: return "Cabeza";
            case PIES: return "Pies";
            default: return "Cuerpo";
        }
    }

    private void ejecutarMuerte(String causa) {
        guardarEcoMuerte();

        StringBuilder sb = new StringBuilder();
        sb.append("=== MUERTE PERMANENTE ===\n");
        sb.append("Causa: ").append(causa).append("\n");
        sb.append("Día: ").append(dia).append("\n");
        sb.append("Tu eco de muerte se guardó.\n");
        sb.append("\nEn futuras partidas, encontrarás tu cadáver.\n");
        sb.append("Recogerlo otorga 1 objeto clave.\n");

        txtMain.setText(sb.toString());

        buttonLayout.removeAllViews();
        addButton("NUEVA PARTIDA", new View.OnClickListener() {
				public void onClick(View v) {
					inicializarJuego();
					verificarEcoMuerte();
					mostrarEstado();
				}
			});
    }

    private void ejecutarVictoria() {
        txtMain.setText("=== RESCATE ===\n\n" +
						"Helicóptero te localiza tras 60 días.\n" +
						"Has sobrevivido el infierno verde.\n" +
						"Pero la selva siempre reclama más...");
        buttonLayout.removeAllViews();
    }
}
