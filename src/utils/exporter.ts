import { jsPDF } from 'jspdf';
import { Task, Category, Priority } from '../types';

/**
 * Generates and downloads a CSV file of all tasks
 */
export function exportTasksToCSV(tasks: Task[]): void {
  const headers = ['ID', 'Titulo', 'Descripcion', 'Categoria', 'Prioridad', 'Completada', 'Fecha Limite', 'Hora', 'Etiquetas', 'Fecha Creacion'];
  
  const rows = tasks.map(t => [
    `"${t.id}"`,
    `"${t.title.replace(/"/g, '""')}"`,
    `"${(t.description || '').replace(/"/g, '""')}"`,
    `"${t.category}"`,
    `"${t.priority}"`,
    `"${t.completed ? 'Si' : 'No'}"`,
    `"${t.dueDate}"`,
    `"${t.dueTime || ''}"`,
    `"${t.tags.join(', ')}"`,
    `"${t.createdAt}"`
  ]);

  const csvContent = [headers.join(','), ...rows.map(e => e.join(','))].join('\n');
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.setAttribute('href', url);
  link.setAttribute('download', `iTask_Pro_Export_${new Date().toISOString().slice(0, 10)}.csv`);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

/**
 * Generates an iCalendar (.ics) string for a list of tasks
 */
export function generateICSContent(tasks: Task[]): string {
  let icsLines = [
    'BEGIN:VCALENDAR',
    'VERSION:2.0',
    'PRODID:-//iTask Pro iOS//Task & Reminder Sync//ES',
    'CALSCALE:GREGORIAN',
    'METHOD:PUBLISH',
    'X-WR-CALNAME:iTask Pro Recordatorios'
  ];

  tasks.forEach(t => {
    const cleanDate = t.dueDate.replace(/-/g, '');
    const timeStr = t.dueTime ? t.dueTime.replace(':', '') + '00' : '090000';
    const dtStart = `${cleanDate}T${timeStr}Z`;
    const uid = `${t.id}-${Date.now()}@itaskpro.app`;

    icsLines.push('BEGIN:VEVENT');
    icsLines.push(`UID:${uid}`);
    icsLines.push(`SUMMARY:${t.title}`);
    icsLines.push(`DESCRIPTION:${t.description || ''} [Prioridad: ${t.priority.toUpperCase()}]`);
    icsLines.push(`DTSTART:${dtStart}`);
    icsLines.push(`DTEND:${dtStart}`);
    icsLines.push(`STATUS:${t.completed ? 'COMPLETED' : 'CONFIRMED'}`);
    icsLines.push(`CATEGORIES:${t.category.toUpperCase()}`);
    if (t.priority === 'critical' || t.priority === 'high') {
      icsLines.push('PRIORITY:1');
    } else if (t.priority === 'medium') {
      icsLines.push('PRIORITY:5');
    } else {
      icsLines.push('PRIORITY:9');
    }
    icsLines.push('END:VEVENT');
  });

  icsLines.push('END:VCALENDAR');
  return icsLines.join('\r\n');
}

/**
 * Downloads the .ics file
 */
export function downloadICSFile(tasks: Task[]): void {
  const content = generateICSContent(tasks);
  const blob = new Blob([content], { type: 'text/calendar;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.setAttribute('href', url);
  link.setAttribute('download', `iTask_Calendar_Sync_${new Date().toISOString().slice(0, 10)}.ics`);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

/**
 * Parses raw .ICS file string into partial Task items
 */
export function parseICSContent(icsText: string): Partial<Task>[] {
  const tasks: Partial<Task>[] = [];
  const events = icsText.split('BEGIN:VEVENT');

  events.slice(1).forEach((ev, idx) => {
    const summaryMatch = ev.match(/SUMMARY:(.*)/);
    const descMatch = ev.match(/DESCRIPTION:(.*)/);
    const dtStartMatch = ev.match(/DTSTART:(.*)/);

    const title = summaryMatch ? summaryMatch[1].trim() : `Evento Importado ${idx + 1}`;
    const description = descMatch ? descMatch[1].trim() : '';
    
    let dueDate = new Date().toISOString().split('T')[0];
    let dueTime = '12:00';

    if (dtStartMatch) {
      const rawDate = dtStartMatch[1].trim();
      if (rawDate.length >= 8) {
        const y = rawDate.slice(0, 4);
        const m = rawDate.slice(4, 6);
        const d = rawDate.slice(6, 8);
        dueDate = `${y}-${m}-${d}`;
      }
      if (rawDate.includes('T') && rawDate.length >= 13) {
        const timePart = rawDate.split('T')[1];
        dueTime = `${timePart.slice(0, 2)}:${timePart.slice(2, 4)}`;
      }
    }

    tasks.push({
      id: `imported-${Date.now()}-${idx}`,
      title,
      description,
      category: 'work' as Category,
      priority: 'medium' as Priority,
      completed: false,
      dueDate,
      dueTime,
      subtasks: [],
      tags: ['Importado', 'iCal'],
      recurrence: 'none',
      calendarSynced: true,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString()
    });
  });

  return tasks;
}

/**
 * Generates an elegant PDF performance and task report using jsPDF
 */
export function generatePDFReport(tasks: Task[], metricsInfo: { totalTasks: number; completedTasks: number; completionRate: number }): void {
  const doc = new jsPDF();
  
  // Header Branding
  doc.setFillColor(15, 23, 42); // slate-900
  doc.rect(0, 0, 210, 35, 'F');
  
  doc.setTextColor(255, 255, 255);
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(22);
  doc.text('iTask Pro iOS', 14, 20);
  
  doc.setFontSize(10);
  doc.setFont('helvetica', 'normal');
  doc.text('Informe Ejecutivo de Rendimiento y Recordatorios', 14, 28);
  doc.text(`Fecha: ${new Date().toLocaleDateString('es-ES')}`, 150, 28);

  // Executive Summary Box
  doc.setFillColor(241, 245, 249); // slate-100
  doc.roundedRect(14, 45, 182, 35, 3, 3, 'F');
  
  doc.setTextColor(30, 41, 59);
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(12);
  doc.text('Resumen de Productividad', 20, 55);
  
  doc.setFont('helvetica', 'normal');
  doc.setFontSize(10);
  doc.text(`Total de Tareas Registradas: ${metricsInfo.totalTasks}`, 20, 65);
  doc.text(`Tareas Completadas: ${metricsInfo.completedTasks}`, 90, 65);
  doc.text(`Tasa de Finalización: ${metricsInfo.completionRate}%`, 150, 65);

  // Task Breakdown Table
  let yPos = 95;
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(14);
  doc.text('Detalle de Tareas y Estado', 14, yPos);
  
  yPos += 8;
  doc.setFillColor(226, 232, 240); // slate-200
  doc.rect(14, yPos, 182, 8, 'F');
  
  doc.setFontSize(9);
  doc.setTextColor(51, 65, 85);
  doc.text('ESTADO', 18, yPos + 5.5);
  doc.text('TÍTULO / DESCRIPCIÓN', 45, yPos + 5.5);
  doc.text('CATEGORÍA', 125, yPos + 5.5);
  doc.text('PRIORIDAD', 155, yPos + 5.5);
  doc.text('FECHA', 180, yPos + 5.5);

  yPos += 10;
  doc.setFont('helvetica', 'normal');

  tasks.slice(0, 18).forEach((t) => {
    if (yPos > 270) {
      doc.addPage();
      yPos = 20;
    }

    const statusText = t.completed ? '[X] Completa' : '[ ] Pendiente';
    const titleShort = t.title.length > 38 ? t.title.slice(0, 35) + '...' : t.title;
    
    if (t.completed) {
      doc.setTextColor(16, 185, 129);
    } else {
      doc.setTextColor(30, 41, 59);
    }
    doc.text(statusText, 18, yPos);
    
    doc.setTextColor(30, 41, 59);
    doc.text(titleShort, 45, yPos);
    
    doc.setTextColor(100, 116, 139);
    doc.text(t.category.toUpperCase(), 125, yPos);
    
    // Priority color
    if (t.priority === 'critical') doc.setTextColor(225, 29, 72);
    else if (t.priority === 'high') doc.setTextColor(234, 88, 12);
    else doc.setTextColor(71, 85, 105);
    doc.text(t.priority.toUpperCase(), 155, yPos);
    
    doc.setTextColor(100, 116, 139);
    doc.text(t.dueDate, 180, yPos);

    yPos += 8;
  });

  // Footer
  doc.setFontSize(8);
  doc.setTextColor(148, 163, 184);
  doc.text('Generado automáticamente por iTask Pro iOS • Modo Offline y Sincronización en Tiempo Real', 14, 285);

  doc.save(`iTask_Reporte_Ejecutivo_${new Date().toISOString().slice(0, 10)}.pdf`);
}
