/**
 * Этот класс позволяет сохранять элементы дерева проекта на сервере.
 * Не важно, будет это весь проект полностью или же какая-либо его состовляющая.
 * Корень проекта хранится в корне сервера в папке
 *              ./user/user_name/project_name/
 * 
 * Файлы проекта хранятся в папке
 *              ./user/user_name/project_name/src/
 * 
 * Содержимое дерева ProjectTree состоит из трех различных частей: 
 *      1. Корень проекта
 *      2. Packages
 *      3. Файлы
 * 
 * По каждому пункту подробнее: 
 * 
 *      1. Корень проекта
 * По сути папка project_name/ в пути ./user/user_name/
 * Для гостя это будет некоторый hash_name, взятый из сессии.
 * 
 *      2. Packages
 * Все пакеты проекта хранятся в папках соотвествующей иерархии.
 * К примеру, если в проекте есть 
 *      package com.javafiddle.web.projecttree
 * то на сервере это будет следующая иерархия папок:
 *              com/
 *              ---javafiddle/
 *              --------------web/
 *              ------------------projecttree/
 * 
 *      3. Файлы
 * Тут все просто. файл просто сохраняется без всяких заморочек в каталог 
 * ProjectNode.parent
 */
package com.javafiddle.web.projecttree;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class ProjectUpdater {
    private ProjectTree tree = new ProjectTree();
    
    public ProjectTree getTree(){
        return tree;
    }
    /**
     * @param element элемент, которые нужно сохранить
     */
    public void save(ProjectNode element) throws IOException{

    }
    
    public void add(String name, String parent){
        tree.add(ProjectNode.makeNode(name, parent, tree));
    }
    
    public void add(ProjectNode node){
        tree.add(node);
    }
}
