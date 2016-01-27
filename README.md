# ADI Quicklook

## ¿Qué es?
**ADI Quicklook** es una *librería agnóstica y extensible* para Android 4.0 o superior, que permite **visualizar de forma rápida** archivos de varias extensiones comunes en la internet. Se creó para su uso con la aplicación de Android de la plataforma **U-Cursos**, de forma de poder visualizar sin necesidad de programas externos los archivos subidos por profesores, auxiliares y alumnos.

## Características principales
Las características actualmente implementadas son las siguientes
* Navegación por carpetas del sistema de archivos.
* Visualización de archivos de texto e imágenes
* Navegación dentro de archivos comprimidos (ZIP, TAR(.GZ), RAR)
* Visualización de archivos PDF.

## Cómo Funciona
La librería consta de dos tipos de estructuras fundamentales:
* El elemento o _Item_ representa abstractamente al documento que será visualizado. Esta estructura guarda en sí la ruta física del archivo, la extensión, el tamaño, el ícono y un fragmento de visualización asociado. La estructura tiene una pequeña API que se puede revisar observando el código para consultas recurrentes.
* El fragmento o _fragment_ en el cual se obserba el elemento. Éste sirve para renderizar el objeto abstracto recibido.
Se define como contenedores virtuales todos los elementos que contienen archivos y no son accesibles directamente desde la memoria física. Algunos ejemplos son los archivos zip, rar, tar y listas de JSON que representan una estructura del estilo.

## Expansibilidad
Es posible expandir la librería con tipos propios, sin necesidad de modificarla internamente. Existen dos tipos de expansión disponibles:

### Expansión para visualización de archivo
Este caso corresponde a los visualizadores de documentos o archivos en específico. Es necesario crear dos archivos:
* Archivo que debe extender la clase _QuicklookFragment_ y debe definir el método onCreateView (Igual que cualquier fragment), mostrando el elemento correspondiente (El cual existe en getItem).
* Archivo que debe extender la clase _FileItem_ y representa abstractamente al documento a visualizar. Se necesita implementar el constructor por defecto de FileItem, llamando a super y posteriormente definiendo la propiedad _image_ como el ícono del elemento, la propiedad _formattedname_ como el nombre a mostrar del elemento y la propiedad _fragment_ como el fragmento asociado.

### Expansión para visualización de contenedor
Este caso corresponde a los visualizadores de contenedores de varios archivos, como por ejemplo los archivos comprimidos o archivos de texto que representan contenedores. Es necesario implementar lo siguiente:
* Método retrieveItem que recibe el id del elemento en que se está, el nombre de la dirección de destino y el contexto de la aplicación. Este método debe tomar el objeto desde donde esté y dejarlo en la dirección solicitada de la carpeta de descargas.
* Método getItemList, el cual consigue la lista completa de elementos que hay dentro del contenedor. Para crear estos elementos se usa el método _createForList(path,type,size,bundle)_ que recibe la dirección interna del elemento, su extensión, su tamaño y un bundle con propiedades extras que se pueden usar tanto en el fragmento asociado como en estos métodos.

Por último, en ambos casos es necesario registrar el nuevo archivo para su uso. Para ello, basta con ejecutar la siguiente línea de código:

`QuicklookActivity.register(Clase.Class,"extension1","extension2"..."extensionN)`

Opcionalmente se puede personalizar las rutas a utilizar para los archivos temporales y la ruta para guardar los archivos cuando se desea:

`QuicklookActivity.setDownloadPath(String path)`
`QuicklookActivity.setCachePath(String path)`

## Librerías utilizadas
*[Junrar 0.7](http://porhacer.com);
*[Apache Commons IO](http://porhacer.com);
*[Apache Commons Compress](http://porhacer.com);
*[PDFView de Joan Zapata](http://porhacer.com);

