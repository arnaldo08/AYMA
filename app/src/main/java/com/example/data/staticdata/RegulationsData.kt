package com.example.data.staticdata

data class Regulation(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: String, // "GENERAL", "CONSTRUCCION", "AGRO", "PROTOCOLOS"
    val year: String,
    val description: String,
    val keyPoints: List<String>,
    val urlRef: String
)

data class ChecklistItemTemplate(
    val itemId: String,
    val text: String,
    val category: String,
    val lawReference: String
)

data class ChecklistTemplate(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val items: List<ChecklistItemTemplate>
)

object RegulationsData {

    val regulations = listOf(
        Regulation(
            id = "LEY_19587",
            title = "Ley N° 19.587",
            subtitle = "Ley de Higiene y Seguridad en el Trabajo",
            category = "GENERAL",
            year = "1972",
            description = "Sancionada en 1972, es la piedra angular de la legislación de Higiene y Seguridad Laboral en la República Argentina. Establece los principios fundamentales para proteger la salud y la integridad física de los trabajadores, prevenir accidentes y enfermedades profesionales en todo el territorio nacional.",
            keyPoints = listOf(
                "Ámbito de aplicación: Todos los establecimientos y explotaciones persigan o no fines de lucro.",
                "Obligaciones del empleador: Adoptar y poner en práctica medidas adecuadas de higiene y seguridad, mantener herramientas en condiciones y capacitar al personal.",
                "Obligaciones del trabajador: Cumplir las normas de seguridad, someterse a exámenes médicos de salud y usar obligatoriamente los equipos de protección (EPP).",
                "Promueve el diseño de locales y puestos de trabajo ergonómicos, térmicamente confortables e iluminados adecuadamente."
            ),
            urlRef = "https://www.argentina.gob.ar/normativa/nacional/ley-19587-17616"
        ),
        Regulation(
            id = "DEC_351_79",
            title = "Decreto N° 351/1979",
            subtitle = "Reglamento General de la Ley de Higiene y Seguridad",
            category = "GENERAL",
            year = "1979",
            description = "Reglamento técnico de cabecera aplicable al sector industrial, comercial y de servicios bajo la órbita de la Ley 19.587. Detalla límites admisibles de contaminantes químicos, físicos, condiciones de ruido, vibraciones, iluminación, carga térmica, instalaciones eléctricas y sistemas contra incendios.",
            keyPoints = listOf(
                "Capítulo 14 - Instalaciones Eléctricas: Exige interruptores disyuntores diferenciales, cableado canalizado y puesta a tierra obligatoria en todas las masas eléctricas.",
                "Capítulo 18 - Protección contra Incendios: Cálculo de carga de fuego, dotación de extintores (matafuegos) con certificación IRAM y señalización de vías de escape claras.",
                "Capítulo 11 - Ventilación y Ventilación Forzada si no se alcanzan condiciones de pureza de aire.",
                "Capítulo 12 - Iluminación: Intensidades mínimas en Lux según tipo de tarea minuciosa o general."
            ),
            urlRef = "https://www.argentina.gob.ar/normativa/nacional/decreto-351-1979-34448"
        ),
        Regulation(
            id = "LEY_13660",
            title = "Ley N° 13.660",
            subtitle = "Seguridad de Instalaciones de Elaboración y Almacenamiento de Combustibles",
            category = "GENERAL",
            year = "1949",
            description = "Sancionada en 1949, regula las condiciones de seguridad en las instalaciones públicas o privadas destinadas a la elaboración, refinación, almacenamiento, fraccionamiento, transporte y distribución de combustibles fluidos o gaseosos, para evitar incendios, explosiones y proteger a la comunidad.",
            keyPoints = listOf(
                "Encuadramiento obligatorio: Establece las normas de seguridad e inspección técnica para destilerías, refinerías, plantas de acopio y estaciones de servicio.",
                "Inscripción en Registros Especiales de la Secretaría de Energía y auditorías periódicas obligatorias de seguridad.",
                "Zonas de Seguridad y distancias mínimas para tanques de almacenamiento respecto de líneas municipales o de edificación.",
                "Sanciones por incumplimiento: Desde multas graves hasta la clausura inmediata de plantas y estaciones de servicio."
            ),
            urlRef = "https://www.argentina.gob.ar/normativa/nacional/ley-13660-159677"
        ),
        Regulation(
            id = "DEC_10877_60",
            title = "Decreto N° 10.877/1960",
            subtitle = "Reglamentación de la Ley 13.660 para el Control de Combustibles",
            category = "GENERAL",
            year = "1960",
            description = "Establece el reglamento detallado de la Ley N° 13.660 de Seguridad de Instalaciones de Combustibles. Define las especificaciones técnicas sobre distancias mínimas, materiales aprobados, venteos, sistemas fijos contra incendios y auditorías obligatorias a cumplir por toda la cadena de hidrocarburos.",
            keyPoints = listOf(
                "Distancias de seguridad: Cuadros técnicos de distancias mínimas exigibles entre tanques de almacenamiento y edificaciones habitadas o linderas.",
                "Sistemas fijos de combate de incendios: Redes de agua con hidrantes, generadores y cámaras de espuma, y muros de contención (piletones) para derrames de tanques.",
                "Pruebas de hermeticidad y presión hidrostática periódicas en tuberías y recipientes de presión.",
                "Requisitos de ventilación y purga de gases inflamables en cámaras, camiones cisterna y tanques subterráneos."
            ),
            urlRef = "https://www.argentina.gob.ar/normativa/nacional/decreto-10877-1960-112702"
        ),
        Regulation(
            id = "LEY_24051",
            title = "Ley N° 24.051",
            subtitle = "Ley de Residuos Peligrosos",
            category = "GENERAL",
            year = "1991",
            description = "Sancionada en 1991, rige la generación, manipulación, transporte, tratamiento y disposición final de residuos peligrosos en el ámbito nacional, cuando se trate de residuos generados o ubicados en lugares bajo jurisdicción nacional, o que puedan afectar a otras provincias o al medio ambiente.",
            keyPoints = listOf(
                "Definición de residuo peligroso: Todo residuo que pueda causar daño a seres vivos, suelo, agua, atmósfera (con las exclusiones de residuos domiciliarios y radioactivos).",
                "Registro y Manifiesto: Obligación para generadores, transportistas y plantas de tratamiento de inscribirse en el Registro Nacional de Generadores y Operadores de Residuos Peligrosos.",
                "Manifiesto de transporte: Documento obligatorio que acompaña el residuo desde el origen hasta su disposición final, garantizando la trazabilidad.",
                "Responsabilidad civil y penal: Establece responsabilidad objetiva del generador por los de residuos, e incluye penas de prisión por contaminar de modo peligroso."
            ),
            urlRef = "https://www.argentina.gob.ar/normativa/nacional/ley-24051-140"
        ),
        Regulation(
            id = "DEC_911_96",
            title = "Decreto N° 911/1996",
            subtitle = "Reglamento para la Industria de la Construcción",
            category = "CONSTRUCCION",
            year = "1996",
            description = "Regula de manera específica las complejas condiciones laborales en la industria de la construcción. Define pautas obligatorias de prevención, supervisión de capataces, habilitación de maquinarias pesadas y protección frente a riesgos críticos de caída de personas u objetos.",
            keyPoints = listOf(
                "Trabajos en Altura: Uso obligatorio de arnés de seguridad anticaídas de cuerpo completo con línea de vida independiente en toda altura superior a 2 metros.",
                "Andamios y Plataformas: Barandas perimetrales (1m de altura), rodapiés (15cm) y superficie de tránsito completa sin tablones sueltos.",
                "Programa de Seguridad: Obligatoriedad de redactar y visar ante la ART un Programa de Seguridad específico ajustado a la etapa constructiva de la obra.",
                "Excavaciones: Inspección previa de suelos, apuntalamiento adecuado, barandas de advertencia perimetral y escaleras de escape rápido."
            ),
            urlRef = "https://www.argentina.gob.ar/normativa/nacional/decreto-911-1996-38148"
        ),
        Regulation(
            id = "DEC_249_07",
            title = "Decreto N° 249/2007",
            subtitle = "Reglamento de Higiene y Seguridad para la Actividad Minera",
            category = "GENERAL",
            year = "2007",
            description = "Reglamento de Higiene y Seguridad adaptado específicamente para la minería (a cielo abierto y subterránea), canteras y prospección. Regula la estabilidad de taludes, ventilación de minas, uso de explosivos y protección especial para los mineros frente al polvo de sílice y gases tóxicos.",
            keyPoints = listOf(
                "Ventilación en minería subterránea: Obligatoriedad de inyectar caudales mínimos de aire limpio según la cantidad de personal y potencia de motores diésel en operación.",
                "Control de polvo y gases: Exámenes para monitorear gases de voladura (CO, NO2) y prevención de la silicosis mediante captación de polvo en húmedo.",
                "Manipulación y almacenamiento de explosivos: Polvorines autorizados, transporte seguro en vehículos especiales y estrictos protocolos para detonaciones.",
                "Estabilidad y labores de sostenimiento: Monitoreo geomecánico de taludes y galerías para prevenir derrumbes catastróficos."
            ),
            urlRef = "https://www.argentina.gob.ar/normativa/nacional/decreto-249-2007-126233"
        ),
        Regulation(
            id = "DEC_1338_96",
            title = "Decreto N° 1338/1996",
            subtitle = "Servicios de Medicina y de Higiene y Seguridad en el Trabajo",
            category = "GENERAL",
            year = "1996",
            description = "Determina las condiciones, responsabilidades y asignación de profesionales para los Servicios de Medicina del Trabajo y de Higiene y Seguridad en el Trabajo, fijando la obligatoriedad de contar con dichos servicios de acuerdo a la cantidad de trabajadores equivalentes y los riesgos de la actividad.",
            keyPoints = listOf(
                "Establece la obligatoriedad de los servicios: de carácter interno o externo, dirigidos por profesionales graduados e inscritos en los registros oficiales.",
                "Cálculo de horas profesionales mensuales: Define fórmulas según el número de trabajadores equivalentes y categoría de riesgo del establecimiento.",
                "Exenciones de servicio: Establece qué tipo de comercios, oficinas o empresas de servicios menores de 15 o 50 trabajadores están exentas de asignación permanente.",
                "Funciones conjuntas: Elaboración de planes de emergencia, capacitación a los trabajadores, exámenes de salud e investigación de accidentes corporales."
            ),
            urlRef = "https://www.argentina.gob.ar/normativa/nacional/decreto-1338-1996-40742"
        ),
        Regulation(
            id = "LEY_24557",
            title = "Ley N° 24.557",
            subtitle = "Ley de Riesgos del Trabajo (LRT)",
            category = "GENERAL",
            year = "1995",
            description = "Regula el sistema nacional de prevención y reparación de contingencias de accidentes de trabajo y enfermedades profesionales. Creó las Aseguradoras de Riesgos del Trabajo (ART) e impuso el control y fiscalización del cumplimiento de normativas de higiene y seguridad por parte de empresas.",
            keyPoints = listOf(
                "Accidentes de Trabajo: Cubre contingencias ocurridas en ocasión del empleo e in-itinere (del trayecto hogar-trabajo).",
                "Denuncia y Cobertura: Recepción médica, farmacológica y prestaciones dinerarias inmediatas e ilimitadas para el trabajador accidentado.",
                "Rol de las ART: Su deber es fiscalizar el cumplimiento de normas de prevención y realizar visitas técnicas obligatorias con planes de mejora.",
                "Listado de Enfermedades Profesionales: Cuadro aprobado por decreto de agentes patógenos que provocan daños prolongados por exposición repetida."
            ),
            urlRef = "https://www.argentina.gob.ar/normativa/nacional/ley-24557-27971"
        ),
        Regulation(
            id = "RES_SRT_311_03",
            title = "Resolución SRT N° 311/2003",
            subtitle = "Reglamento de Higiene y Seguridad para TV por Cable y Telecomunicaciones",
            category = "PROTOCOLOS",
            year = "2003",
            description = "Reglamento específico aprobado por la Superintendencia de Riesgos del Trabajo para el personal de instalación, mantenimiento y operación de televisión por cable, internet y servicios de telecomunicaciones afines, con énfasis en tareas de altura, postes y riesgo eléctrico.",
            keyPoints = listOf(
                "Trabajos en postes y apoyo: Verificación previa del estado del poste (podredumbre, rajaduras) antes de ascender, y uso de escaleras dieléctricas y cinturón de seguridad o arnés.",
                "Operación en cercanía de redes eléctricas: Respetar rigurosamente las distancias de seguridad respecto a líneas de baja, media o alta tensión.",
                "Elementos de protección obligatorios: Guantes dieléctricos, calzado dieléctrico, casco de seguridad de ala cerrada y ropa de trabajo de material ignífugo o dieléctrico.",
                "Capacitación obligatoria: Instrucción de RCP avanzado y técnicas de autoevacuación y rescate en altura para todas las cuadrillas operativas."
            ),
            urlRef = "https://www.argentina.gob.ar/normativa/nacional/resolución-311-2003-88892"
        ),
        Regulation(
            id = "RES_SRT_905_15",
            title = "Resolución SRT N° 905/2015",
            subtitle = "Funciones Obligatorias de Higiene y Seguridad y Medicina del Trabajo",
            category = "PROTOCOLOS",
            year = "2015",
            description = "Unifica, detalla y operativiza las funciones conjuntas que deben desarrollar obligatoriamente los profesionales de las áreas de Higiene y Seguridad y de Medicina del Trabajo en toda empresa, consolidando la prevención integral y el seguimiento del estado de salud de los empleados.",
            keyPoints = listOf(
                "Elaboración de Diagnosis Inicial y Plan de Trabajo Anual firmado con objetivos medibles de tasa de siniestralidad del establecimiento.",
                "Exámenes de salud coordinados: de ingreso, periódicos, previos a transferencia de tareas, tras ausencias prolongadas y de egreso.",
                "Custodia de documentación obligatoria: Conservar el Legajo Técnico de Higiene y Seguridad e Historial Clínico Laboral confidencial por un mínimo de 40 años.",
                "Capacitación continua: Programa de Capacitación Anual obligatoria firmado al personal sobre primeros auxilios, RCP básico, riesgos del puesto y adicciones."
            ),
            urlRef = "https://www.argentina.gob.ar/normativa/nacional/resolución-905-2015-246536"
        ),
        Regulation(
            id = "RES_SRT_85_12",
            title = "Resolución SRT N° 85/2012",
            subtitle = "Protocolo de Medición de Ruido en Ambiente Laboral",
            category = "PROTOCOLOS",
            year = "2012",
            description = "Establece el protocolo oficial obligatorio para medir y asentar la exposición del personal al ruido continuo o impulsivo en ambientes laborales. Unifica los criterios técnicos de sonometría y dosimetría para certificar que ningún empleado supere la dosis perjudicial.",
            keyPoints = listOf(
                "Nivel Límite de Exposición: Máximo de 85 dB(A) para una jornada estándar de 8 horas diarias de labor.",
                "Instrumental calibrado: Mediciones mediante decibelímetro (sonómetro) clase 1 o 2 con certificado de calibración anual al día.",
                "Planilla Oficial obligatoria anexada al legajo técnico de Higiene y Seguridad de la empresa.",
                "Protección auditiva: En zonas de superación, obligación de implementar EPP adecuados de copa o inserción, reduciendo teóricamente el nivel por debajo de 80 dB(A)."
            ),
            urlRef = "https://www.argentina.gob.ar/normativa/nacional/resolución-85-2012-195973"
        ),
        Regulation(
            id = "RES_SRT_84_15",
            title = "Resolución SRT N° 84/2015",
            subtitle = "Protocolo de Medición de Iluminación Obligatorio",
            category = "PROTOCOLOS",
            year = "2015",
            description = "Unifica las prácticas y exigencias para el relevamiento de iluminación en plantas industriales, oficinas o locales comerciales. Evita la fatiga visual, cefaleas o accidentes graves causados por la penumbra o reflejos deslumbrantes en el plano de trabajo.",
            keyPoints = listOf(
                "Instrumental: Uso de luxómetros de precisión provistos de filtros fotópicos calibrados.",
                "Puntos de medición: Determinación sistemática del promedio en pasillos, accesos, áreas funcionales y sobre el plano exacto donde opera el operario.",
                "Contraste y Deslumbramiento: Análisis de deslumbramiento directo e indirecto generado por lámparas defectuosas o luz natural reflejada.",
                "Vigencia: Tiene carácter anual y debe registrar firmas conjuntas del responsable técnico de Seguridad e Higiene."
            ),
            urlRef = "https://www.argentina.gob.ar/normativa/nacional/resolución-84-2015-242371"
        ),
        Regulation(
            id = "RES_SRT_886_15",
            title = "Resolución SRT N° 886/2015",
            subtitle = "Protocolo Obligatorio para Ergonomía Laboral",
            category = "PROTOCOLOS",
            year = "2015",
            description = "Reemplaza regulaciones anteriores y sistematiza las metodologías científicas para detectar, evaluar preliminarmente y corregir factores de riesgo ergonómico. Aborda lesiones musculoesqueléticas ocasionadas por esfuerzos excesivos, movimientos de pinza repetidos o posturas forzadas sostenedoras.",
            keyPoints = listOf(
                "Planilla de Identificación de Factores de Riesgo (Etapa 1): Mapeo integral de cada puesto para categorizar peligros (Ej. rotación de tronco, torsión de cuello).",
                "Evaluaciones Avanzadas (Etapa 2): Uso de normas ISO 11228, método NIOSH, RULA o REBA si se encuentra sospecha moderada o alta.",
                "Medidas de Intervención: Reformulación de la altura de mesadas de empaque, provisión de elevadores neumáticos, estribos para pies o sillas ergonómicas.",
                "Pausas Activas: Consiste en micro-sesiones obligatorias de estiramiento muscular durante los turnos."
            ),
            urlRef = "https://www.argentina.gob.ar/normativa/nacional/resolución-886-2015-253322"
        ),
        Regulation(
            id = "DEC_617_97",
            title = "Decreto N° 617/1997",
            subtitle = "Reglamento de Higiene y Seguridad Agraria",
            category = "AGRO",
            year = "1997",
            description = "Reglamento nacional adaptado exclusivamente para las faenas rurales, de cría de ganado, agroindustriales básicas y labranza de campos. Considera los riesgos del manejo de agroquímicos inflamables o tóxicos, picaduras de insectos o mordeduras de ofidios, y vuelcos de tractores.",
            keyPoints = listOf(
                "Protección en maquinaria: Proteger correas, poleas, tomas de fuerza y habilitación de estructuras antivuelco (cabinas ROPS) en tractores.",
                "Agroquímicos y plaguicidas: Depósitos exclusivos ventilados, rotulación de envases químicos según sistema SGA, triple lavado e inutilización de envases vacíos.",
                "Estructuras y Silos: Protección anticaídas en escaleras de silos altos, y uso de arnés y detector de gases antes del ingreso de un operario por riesgo de asfixia.",
                "Higiene Básica: Suministro de agua potable para beber e higiene diaria personal incluso a pie de surco."
            ),
            urlRef = "https://www.argentina.gob.ar/normativa/nacional/decreto-617-1997-42589"
        )
    )

    val templates = listOf(
        ChecklistTemplate(
            id = "TEMP_ISO_351",
            title = "Inspección General de Establecimiento",
            category = "Decreto 351/79",
            description = "Listado adaptado a las exigencias regulatorias anuales de comercios e industrias generales en Buenos Aires y provincias de la República Argentina.",
            items = listOf(
                ChecklistItemTemplate("351_EPP", "¿Se suministran EPP adecuados al puesto y cuentan con la firma en el registro individual de entrega?", "Equipos de Protección", "Dec. 351/79 Art. 188"),
                ChecklistItemTemplate("351_ORDEN", "¿Los pisos de tránsito, escaleras y pasillos de evacuación están libres de tropiezos o aceites?", "Orden y Limpieza", "Dec. 351/79 Art. 42"),
                ChecklistItemTemplate("351_MATAFUEGO", "¿Los matafuegos tienen oblea de carga vigente, se hallan colgados a 1.20m y señalizados con balizas?", "Prevención Incendios", "Dec. 351/79 Art. 175"),
                ChecklistItemTemplate("351_ELECTRICO", "¿Los tableros principales tienen puesta a tierra, interruptor disyuntor y tapa de protección contra contactos accidentales?", "Riesgo Eléctrico", "Dec. 351/79 Art. 95"),
                ChecklistItemTemplate("351_ILUM", "¿Hay niveles óptimos de luz artificial (evitando parpadeos o zonas ciegas) en planos operativos?", "Servicios de Planta", "Dec. 351/79 Art. 71"),
                ChecklistItemTemplate("351_BOTIQUIN", "¿Se dispone de botiquín de primeros auxilios completo, señalizado y con medicinas no vencidas?", "Primeros Auxilios", "Dec. 351/79 Art. 187")
            )
        ),
        ChecklistTemplate(
            id = "TEMP_CONST_911",
            title = "Inspección de Obras y Construcción",
            category = "Decreto 911/96",
            description = "Foco en etapas iniciales de excavación, movimiento de tierras, hormigonado y trabajos de elevación en andamios colgantes o de marco.",
            items = listOf(
                ChecklistItemTemplate("911_ARNES", "¿Los operarios efectúan trabajos en altura (> 2m) asegurados con arnés reglamentario y cabo de vida doble?", "Trabajos en Altura", "Dec. 911/96 Art. 54"),
                ChecklistItemTemplate("911_ANDAMIO", "¿Los andamios tubulares poseen apoyos nivelados sobre tacos, diagonales trabadas, baranda doble y tablado completo?", "Estructuras de Obra", "Dec. 911/96 Art. 222"),
                ChecklistItemTemplate("911_CASCO", "¿Todo el personal y las visitas ingresan y transitan obligatoriamente con casco de obra y botín de seguridad?", "Uso de EPP", "Dec. 911/96 Art. 120"),
                ChecklistItemTemplate("911_EXCAV", "¿Las excavaciones de más de 1.20 metros tienen taludes laterales o apuntalados rígidos para evitar desmoronamientos?", "Excavaciones", "Dec. 911/96 Art. 142"),
                ChecklistItemTemplate("911_DIFERENCIAL", "¿La red eléctrica provisoria de obra cuenta con un tablero estanco provisto de disyuntor diferencial de 30mA?", "Instalación Provisoria", "Dec. 911/96 Art. 90"),
                ChecklistItemTemplate("911_CARTELES", "¿La obra está correctamente delimitada por cerco perimetral e incluye señalización acústica-óptica para peatones externos?", "Señalización", "Dec. 911/96 Art. 45")
            )
        ),
        ChecklistTemplate(
            id = "TEMP_FIRE_PREV",
            title = "Prevención de Incendios y Evacuación",
            category = "Siniestros e Emergencias",
            description = "Evaluación detallada para planes de autoprotección vigentes bajo normas del cuerpo de bomberos voluntario y de la SRT.",
            items = listOf(
                ChecklistItemTemplate("FIRE_PANIC", "¿Las puertas de salida de emergencia cuentan con barra antipánico y abren hacia afuera en el sentido de flujo?", "Salidas Escolares/Fabril", "Ley 19.587 Cap. 18"),
                ChecklistItemTemplate("FIRE_LIGHT", "¿Las luces de emergencia de estado autónomo se encienden al cortar el interruptor general?", "Sistemas Eléctricos", "Dec. 351/79 Art. 165"),
                ChecklistItemTemplate("FIRE_ROLES", "¿Existe un cuadro organizativo de roles asignado en simulacro de evacuación, señalando coordinadores y brigadistas?", "Organización", "Dec. 351/79 Art. 187"),
                ChecklistItemTemplate("FIRE_ALARMA", "¿La sirena o alarma centralizada de alerta es audible e identificable por encima de los ruidos usuales de la planta?", "Sistemas Sonoros", "Dec. 351/79 Art. 182"),
                ChecklistItemTemplate("FIRE_BALDE", "¿Se registran baldes de arena o sustancias absorbentes en el sector de acopio provisional de combustibles?", "Área de Acopio", "Dec. 351/79 Art. 177")
            )
        ),
        ChecklistTemplate(
            id = "TEMP_ERGO_886",
            title = "Ergonomía y Carga de Trabajo",
            category = "Resolución 886/15",
            description = "Auditoría ergonómica periódica de puestos de oficina administrativa, centros de llamadas o embaladores industriales.",
            items = listOf(
                ChecklistItemTemplate("ERGO_PESO", "¿Se evita que un mismo operario manipule de manera manual cargas individuales directas superiores a 25 kg?", "Esfuerzo Físico", "Res. SRT 886/15 Criterio A"),
                ChecklistItemTemplate("ERGO_SILLA", "¿La silla de trabajo de escritorio posee cinco ruedas de base, asiento regulable y respaldo reclinable?", "Puestos de Trabajo", "Dec. 351/79 Anexo I"),
                ChecklistItemTemplate("ERGO_PANTALLA", "¿La pantalla está posicionada de modo tal que el borde superior se sitúe a la altura horizontal de los ojos del usuario?", "Pantallas de Datos", "Res. SRT 886/15 Criterio B"),
                ChecklistItemTemplate("ERGO_PAUSAS", "¿Se implementan minutos reglados de pausas activas para realizar elongaciones en jornadas prolongadas?", "Organización Laboral", "Res. SRT 886/15 Criterio C"),
                ChecklistItemTemplate("ERGO_REPETITIVO", "¿Se evitan tareas de digitación manual continuas sin alternancia de tareas por más de dos horas seguidas?", "Movimientos Repetidos", "Res. SRT 886/15 Criterio D")
            )
        )
    )

    fun getTemplateById(id: String): ChecklistTemplate? {
        return templates.find { it.id == id }
    }
}
