import React from 'react';
import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { Card } from '../../../../ui/card';
import { Label } from '../../../../ui/label';
import { Button } from '../../../../ui/button';
import { Edit2, Eye, Trash2 } from 'lucide-react';

export function SortableField({ field, disabled, onEdit, onDelete, renderField,preview }) {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id: field.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };

  return (
    <div ref={setNodeRef} style={style} className="space-y-2">
      <Card className="p-4">
        <div className="flex items-center justify-between">
          <div
            {...attributes}
            {...listeners}
            className="flex-1"
          >
            {/* <Label>{field.label}</Label> */}
            {renderField(field, isDragging)}
          </div>
          {!disabled && !preview && (
            <div className="flex items-center space-x-2">
              <Button
                variant="ghost"
                size="icon"
                onClick={onEdit}
              >
                <Edit2 className="h-4 w-4" />
              </Button>
              <Button
                variant="ghost"
                size="icon"
                onClick={onDelete}
              >
                <Trash2 className="h-4 w-4" />
              </Button>
            </div>
          )}
        </div>
      </Card>
    </div>
  );
}
